# SvnMerkleizer

Adds Merkle-tree functionality to a Subversion install

* Merkle Tree is kept fresh on the server-side for each user, obeying their potentially different read permissions 

Limitations:

* MOD_DAV/Apache only (not the binary protocol)
* Subversion doesn't know anything about this:
  * The endoints for this are outside Subversion's understanding
  * History of the Merkle tree changed are not retained
  
# How it works.

When it receives a GET to /foo/bar/baz/.merkle this service handles the request and issues a number of 
PROPFIND an OPTIONS (WebDAV) operations to Subversion via Apache and MOD_DAV_SVN. Then it caches the the result
and send it to the requester. Depending on the client library and how out of date the tree is, the GET request could 
timeout. The backend would contiue calculating the tree, and a subsequent GET request for the same URL would
be quicker.

Obviously that's a bunch of IO that makes it all imperfect.  This can be mitigated by movin the SvnMerkleizer service
closet in terms of TCP/IP to the Apache/Subversion machine(s). I have a Docker image that is Apache, Subversion and the 
SvnMerkleizer service in one image. That's unorthodox as you're supposed to have a single process(*) in a docker
container, to allow smooth stutdown and restarts.

## Different Merkle trees for different Subversion users

Subversion allows uers and groups of users to have different permissions (read and write) for directories and
files within the same repo. Subversion has has a coupled "Authz" technology that uses a textual grammar to
represent the users and group permissions. This "Authorization" technology is secondary to authentication which 
itself is pluggable in Subversion/Apache. SvnMerkleizer calulates a Merkle tree for each user, and attempts to
utilize the same tree where more than one user has the same Authz setup. It processes the Authz file at startup 
to determine any similarities toward that.  The supported permutations of Authz settings are shown in 
[AuthzExamples.md](AuthzExamples.md). Indeed that source file is used in unit tests.

## Modes of operation. 

As well as the GET-centric hidden `.merkle` resources, there is a alternate setup that uses a new HTTP verb/method
on the directory (URL). You would deploy one or the other.  You could for example have a method 'MERKLETREE' 
instead of GET.

# Building it.

It is written in Java and uses Maven for the build `mvn install -DskipTests`. If you want to run all tests, you will have 
to stand up a testing instance of Subversion. I have a handy Docker solution for that here: 
[svnmerkleizer-test-repo](https://github.com/paul-hammant-fork/svnmerkleizer-test-repo) (instructions in that repo).

## Servirtium usage

Within SvnMerkleizer build, there is a use of "Servirtium" to allow playback of the previously recorded 
`svnmerkleizer-test-repo` interactions. This means you don't have to launch the latter with Docker. To see integration 
tests run with Servirtium emulating Subversion + MOD_DAV_SVN + Apache, do:

```
mvn install -Dtest=T4_PlayedBackSubversionServiceTests
```
   
The recorded conversations are stored (src/test/mocks - TODO)[]   
   
If you have `svnmerkleizer-test-repo` running per it's instructions, you can re-record the interactions for the same 
service test: 

```
mvn install -Dtest=T3_RecordedSubversionServiceTests
```

This overwrites the pertinent markdown sources in `src/test/mocks`. If you then do `git diff` and there are any
differences you're perhaps seeing an incompatibility. I've blogged about TCKs for the usefulness of that.
  
The same tests are run for `T3_RecordedSubversionServiceTests`  and `T4_PlayedBackSubversionServiceTests`. For that
matter, the same tests are run for `T0_DirectServiceTests` and for that test class, there's no use of Servirtium 
at all.
    
# Comparisons to "out of the box" Git

Git breaths the SHA1 hashes of files, and changes. It is possible (if history has not been purged/rewritten) 
to bring back a repo to exact same content represented by a commit hash. At any time, the correctness of Git's
Merkle tree over content and meta data can be verified.  SvnMerkleizer is not holding history of the Merkle tree 
(see Directions below). You could verify the entre merkle tree as you have it, but if the canonical server repo gets
updated, the only thing you know now about your client side 'checkout' is that the tree is out of date.
  
Git isn't very good at large binaries or terrabytes of history though. You could say that Git-LFS delivers on the 
former, but I might like to see something more built-in.  Git also doesn't allow per-user or per-group read/write
permissions for directories/files, whereas Subversion does. Perhaps Git will deliver on all of these in the fullness
of time. Or a successor takes over that's not objectionable to the Git intelligentsia.  
  
# Alternatives to SvnMerkleizer

A better solution would be to have a Rust commit hook that recalulcated the merkle tree for the in-progress commit on 
the server, and wrote .merkle files to the pertinent directories, and included them in the commit itself. That'd could 
force the committer to do a 'svn up' operation immediately following the commit, but that should be quick (and never 
clash). I say could, because Subversion does not require to to be "up to date" with working copy before you commit, 
unless one of the files your trying to change has changed server-side too. The downside of a commit-hook equivalent is 
that it can only really be for a single 'admin' user, and if you yourself did a checkout and you're only permitted to 
see a subset of the directories/files in the repo, then you'll not be able to verify the the merkle tree (because 
branches are hidden from you).