# SvnMerkleizer


Features:

* Adds Merkle-tree functionality to a Subversion install.
* The Merkle tree is kept up to date on the server-side for all users, obeying each user's potentially different read permissions 

Limitations:

* MOD_DAV/Apache only - not the binary Subversion (svn://) protocol
* Subversion does not know anything about:
  * The endpoints for this are outside Subversion's understanding
  * History of the Merkle tree changes are not retained
  
# Merkle tree recap 

A system of using hashes (SHA1 in Subversion's case) to track a directory tree of resources to allow fast determination 
of whether "your" tree is out of step with "their" tree, and from that efficiently determine what needs to be 
updated in order to keep you in step. It is a field of science in itself ([Ralph Merkle invented this in 1979](https://en.wikipedia.org/wiki/Merkle_tree)), 
and underpins BlockChain, though vanilla Merkle Trees are not blockchains, and massively under appreciated in the 
industry and indeed society. 
  
# How it works.

When it receives a GET to /foo/bar/baz/.merkle this service handles the request and issues a number of 
PROPFIND an OPTIONS (WebDAV) operations to Subversion via Apache and MOD_DAV_SVN. The SvnMerkleizer tech
does a depth first traversal of the director tree working out where its SHA1 directory hash is out of 
date and needs recalculating.

Then it caches all results for directories and sends the specific result sought back to the requester. 
Depending on the client library and how out of date the tree is, the GET request could 
timeout. The backend would continue calculating the tree, and a subsequent GET request for the same URL would
be quicker in practice. It is possible that you could GET the root diretory's SHA1 with a cron job to keep it 
fresh ahead of need.

Obviously that's a bunch of IO that makes it all imperfect.  This can be mitigated by moving the SvnMerkleizer service
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
[SvnMerkleizer-test-repo](https://github.com/paul-hammant-fork/SvnMerkleizer-test-repo) (instructions in that repo). 
Be aware that the integration tests for SvnMerkleizer are many minutes long, and not needed if you're only trying 
to play with this technology.

## Servirtium usage in test automation for this

Within SvnMerkleizer build, there is a use of [Servirtium](https://github.com/paul-hammant/servirtium) to allow playback of the previously recorded 
`SvnMerkleizer-test-repo` interactions. This means you don't have to launch the latter with Docker. To see integration 
tests run with Servirtium emulating Subversion + MOD_DAV_SVN + Apache, do:

```
mvn install -Dtest=PlayedBackSubversionServiceTests
```
   
The recorded conversations are stored [src/test/mocks/subversion](https://github.com/paul-hammant/SvnMerkleizer/tree/master/src/test/mocks/subversion)   
   
If you have `SvnMerkleizer-test-repo` running per it's instructions, you can re-record the interactions for the same 
service test: 

```
mvn install -Dtest=RecordedSubversionServiceTests
```

This overwrites the pertinent markdown sources in `src/test/mocks`. If you then do `git diff` and there are any
differences you're perhaps seeing an incompatibility. I've blogged about TCKs for the usefulness of that.
  
The same tests are run for `RecordedSubversionServiceTests` and `PlayedBackSubversionServiceTests`. For that
matter, the same tests are run for `DirectServiceTests` and for that test class, there's no use of Servirtium 
at all.
    
## Running it    

In Maven terms depend on SvnMerkleizer 

```
<dependency>
    <groupId>com.paulhammant.svnmerkleizer</groupId>
    <artifactId>svnmerkleizer-service</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>    
```

And one of three HTTP servers abstracted by the excellent Jooby:

```
<dependency>
    <groupId>org.jooby</groupId>
    <artifactId>jooby-netty</artifactId>
    <!-- 
      Two alternates:
    <artifactId>jooby-jetty</artifactId>
    <artifactId>jooby-undertow</artifactId>
                                        -->
    <version>1.6.1</version>
    <optional>true</optional>
</dependency>
```

Then pick one of the ways of [booting it](https://github.com/paul-hammant/SvnMerkleizer/tree/master/src/main/java/com/paulhammant/svnmerkleizer/boot), 
and deploy it close to your Subversion/Apache service (minimizing HTTP distance is a good idea as it is chatty).

Get acquainted as to how by looking at a Docker image that puts it all together: [SvnMerkleizer-all-in-one](https://github.com/paul-hammant/SvnMerkleizer-all-in-one) 

# License

BSD 2-Clause license (open source).

# Usefuleness
    
All sorts of super huge record keeping applications. Perhaps ones where the change rate does not exceed one change every 
10 seconds. Larger changes (say video files) will be much longer than 10 seconds each, but storable in Subversion.
If you deploy with a Subversion install that has `SVNAutoversioning on` then you can do HTTP PUTs of resources in 
addition to commits, and that would suit a file-sync agent.  Indeed
    
# Comparisons to "out of the box" Git for Merkle-tree goodness

Git breathes the SHA1 hashes of files, and changes. It is possible (if history has not been purged/rewritten via force-push) 
to bring back a repo to exact same content represented by a commit hash. At any time, the correctness of Git's
Merkle tree over content and meta data can be verified.  SvnMerkleizer is not holding history of the Merkle tree 
(see "Alternatives" below). You could verify the entire Merkle tree as you have it, but if the canonical server repo gets
updated, the only thing you know now about your client-side 'checkout' is that the tree is different to the server one 
(out of date).
  
Git isn't very good at large binaries or terabytes of history though. You could say that Git-LFS delivers on the 
former, but I might like to see something more built-in.  

Git also doesn't allow per-user or per-group read/write
permissions for directories/files, whereas Subversion does. Perhaps Git will deliver on all of these in the fullness
of time. Or a successor takes over that's not objectionable to the Git intelligentsia.  
  
That said, I'm willing to say that Git is a near perfect history-retaining Merkle tree.
  
# Alternatives to SvnMerkleizer

A better solution would be to have a Rust/Nim/Zig (or C) commit hook that recalculated the merkle tree for the 
in-progress commit on 
the server, and wrote .merkle files to the pertinent directories, and included them in the commit itself. That'd could 
force the committer to do a 'svn up' operation immediately following the commit, but that should be quick (and never 
clash). I say could, because Subversion does not require to to be "up to date" with working copy before you commit, 
unless one of the files your trying to change has changed server-side too. The downside of a commit-hook equivalent is 
that it can only really be for a single 'admin' user, and if you yourself did a checkout and you're only permitted to 
see a subset of the directories/files in the repo, then you'll not be able to verify the the merkle tree (because 
branches are hidden from you).