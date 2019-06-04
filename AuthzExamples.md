# Authz examples

Some cases not handles yet

## Basic

Blah blah

```
# User defined in group

[groups]
admin = boss, sysadmin
phpteam = php1, php2, php3
rubyteam = ruby1, ruby2
 
# SVN root should only be access by boss and system admin
[/]
@admin = rw
* =
 
# Main web repository should only be access by boss and system admin, others cannot read/write at all
# By specifying svn path, we need to list which user/group who can and cannot access
[/web]
@admin = rw
* =
 
# PHP project repository can only be access by php developer, boss and sys admin
# By specifying svnrep: directive, we just need to list which user/group who can access
# Others will automatically rejected
[svnrep:/web/php]
@phpteam = rw
@admin = rw
 
# Ruby project repository can only be access by Ruby developer, boss and sys admin
# By specifying svnrep: directive, we just need to list which user/group who can access
# Others will automatically rejected
[svnrep:/web/ruby]
@rubyteam = rw
@admin = rw
```

## Another

Blah blah

```
[/]
*=

[groups]
GroupA=john,sally
GroupB=harry,george

[repo:/Project1]
@GroupA=rw

[repo:/Project2]
@GroupA=rw
@GroupB=rw
```

## Another

```
[/]
*=

[groups]
GroupA=john,sally
GroupB=john,george

[repo:/Project1]
@GroupA=rw

[repo:/Project2]
@GroupA=rw
@GroupB=rw
```

## Another

```
[/]
*=

[groups]
GroupA=john
GroupB=john

[repo:/Project1]
@GroupA=rw

[repo:/Project2]
@GroupA=rw
@GroupB=rw
```

## Another

```
[/]
*=

[groups]
GroupA=x,john
GroupB=y,john

[repo:/Project1]
@GroupA=rw

[repo:/Project2]
@GroupA=rw
@GroupB=rw
```

## Another

```
[calc:/branches/calc/bug-142]
harry = rw
sally = r
```

## Another

```
[calc:/branches/calc/bug-142]
harry = rw
sally = r

# give sally write access only to the 'testing' subdir
[calc:/branches/calc/bug-142/testing]
sally = rw
```

## Another

```
[calc:/branches/calc/bug-142]
harry = rw
sally = r

[calc:/branches/calc/bug-142/secret]
harry =
```

## Another

```
[/]
* = r
```

## Another

```
```