## Interaction 0: GET /abc123/A/AK/.merkle.csv

## [Note] Test Context:

.merkle.csv retrieves CSV

### Request headers recorded for playback:

```
Accept-Encoding: gzip,deflate
Accept: */*
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
Host: localhost:8080
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
```

### Request body recorded for playback ():

```

```

### Response headers recorded for playback:

```
Content-Type: text/csv;charset=UTF-8
connection: keep-alive
content-length: 136
```

### Response body recorded for playback (200: text/csv;charset=UTF-8):

```
,d19e7b3cade2b87a5031c71855191637ea8835b1
A/,646f8a2439291fccbab6d9419ae0aa1b57a0d67b
hello.txt,f572d396fae9206628714fb2ce00f72e94f2258f
```

## Interaction 1: GET /abc123/A/AK/.merkle.txt

## [Note] Test Context:

.merkle.txt retrieves TXT

### Request headers recorded for playback:

```
Accept-Encoding: gzip,deflate
Accept: */*
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
Host: localhost:8080
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
```

### Request body recorded for playback ():

```

```

### Response headers recorded for playback:

```
Content-Type: text/plain;charset=UTF-8
connection: keep-alive
content-length: 135
```

### Response body recorded for playback (200: text/plain;charset=UTF-8):

```
d19e7b3cade2b87a5031c71855191637ea8835b1
A/ 646f8a2439291fccbab6d9419ae0aa1b57a0d67b
hello.txt f572d396fae9206628714fb2ce00f72e94f2258f
```

## Interaction 2: GET /abc123/A/AK/.merkle.csv

## [Note] Test Context:

.merkle.csv does not work if preemptive bad password

### Request headers recorded for playback:

```
Accept-Encoding: gzip,deflate
Accept: */*
Authorization: Basic aGFycnlwdzpzZGZzZGZld2Vyd2Vyd2Vyd2U=
Connection: keep-alive
Host: localhost:8080
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
```

### Request body recorded for playback ():

```

```

### Response headers recorded for playback:

```
WWW-Authenticate: Basic realm="Subversion Repository"
content-length: 0
```

### Response body recorded for playback (401: null - Base64 below):

```

```

## Interaction 3: GET /abc123/A/AK/.merkle.xml

## [Note] Test Context:

.merkle.xml retrieves XML

### Request headers recorded for playback:

```
Accept-Encoding: gzip,deflate
Accept: */*
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
Host: localhost:8080
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
```

### Request body recorded for playback ():

```

```

### Response headers recorded for playback:

```
Content-Type: text/xml;charset=UTF-8
connection: keep-alive
content-length: 282
```

### Response body recorded for playback (200: text/xml;charset=UTF-8):

```
<directory>
  <sha1>d19e7b3cade2b87a5031c71855191637ea8835b1</sha1>
  <entry>
    <dir>A</dir>
    <sha1>646f8a2439291fccbab6d9419ae0aa1b57a0d67b</sha1>
  </entry>
  <entry>
    <file>hello.txt</file>
    <sha1>f572d396fae9206628714fb2ce00f72e94f2258f</sha1>
  </entry>
</directory>
```

## Interaction 4: GET /abc123/A/AK/.merkle.html

## [Note] Test Context:

.merkle.html retrieves HTML

### Request headers recorded for playback:

```
Accept-Encoding: gzip,deflate
Accept: */*
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
Host: localhost:8080
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
```

### Request body recorded for playback ():

```

```

### Response headers recorded for playback:

```
Content-Type: text/html;charset=UTF-8
connection: keep-alive
content-length: 266
```

### Response body recorded for playback (200: text/html;charset=UTF-8):

```
<html><body>
<p>d19e7b3cade2b87a5031c71855191637ea8835b1</p>
<table>
<tr><td><a href="A/.merkle.html">A</a></td><td>646f8a2439291fccbab6d9419ae0aa1b57a0d67b</td></tr>
<tr><td>hello.txt</td><td>f572d396fae9206628714fb2ce00f72e94f2258f</td></tr>
</table></body></html>
```

## Interaction 5: GET /abc123/blah/blah/.merkle.csv

## [Note] Test Context:

bogus URL results in 404 response

### Request headers recorded for playback:

```
Accept-Encoding: gzip,deflate
Accept: */*
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
Host: localhost:8080
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
```

### Request body recorded for playback ():

```

```

### Response headers recorded for playback:

```
content-length: 0
```

### Response body recorded for playback (404: null - Base64 below):

```

```

## Interaction 6: GET /blah/blah/.merkle.csv

## [Note] Test Context:

another bogus URL results in 404 response

### Request headers recorded for playback:

```
Accept-Encoding: gzip,deflate
Accept: */*
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
Host: localhost:8080
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
```

### Request body recorded for playback ():

```

```

### Response headers recorded for playback:

```
Cache-Control: must-revalidate,no-cache,no-store
Content-Type: text/html;charset=UTF-8
connection: keep-alive
content-length: 0
```

### Response body recorded for playback (404: text/html;charset=UTF-8):

```

```

## Interaction 7: GET /abc123/A/AK/.merkle.json

## [Note] Test Context:

.merkle.json retrieves JSON

### Request headers recorded for playback:

```
Accept-Encoding: gzip,deflate
Accept: */*
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
Host: localhost:8080
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
```

### Request body recorded for playback ():

```

```

### Response headers recorded for playback:

```
Content-Type: application/json;charset=UTF-8
connection: keep-alive
content-length: 245
```

### Response body recorded for playback (200: application/json;charset=UTF-8):

```
{
  "sha1" : "d19e7b3cade2b87a5031c71855191637ea8835b1",
  "contents" : [ {
    "dir" : "A",
    "sha1" : "646f8a2439291fccbab6d9419ae0aa1b57a0d67b"
  }, {
    "file" : "hello.txt",
    "sha1" : "f572d396fae9206628714fb2ce00f72e94f2258f"
  } ]
}
```

