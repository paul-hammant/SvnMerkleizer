## Interaction 0: GET /abc123/A/AK/.merkle.csv

## [Note] Test Context:

.merkle.csv does not work with no attempt to authenticate

### Request headers recorded for playback:

```
Connection: keep-alive
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
Host: localhost:8080
Accept-Encoding: gzip,deflate
Accept: */*
Content-Length: 0
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

## Interaction 1: GET /abc123/A/AK/.merkle.csv

## [Note] Test Context:

.merkle.csv works after idiomatic authentication challenge

### Request headers recorded for playback:

```
Connection: keep-alive
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
Host: localhost:8080
Accept-Encoding: gzip,deflate
Accept: */*
Content-Length: 0
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

## Interaction 2: GET /abc123/A/AK/.merkle.csv

### Request headers recorded for playback:

```
Authorization: Basic aGFycnk6aGFycnlwdw==
Accept: */*
Connection: keep-alive
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
Host: localhost:8080
Accept-Encoding: gzip,deflate
Content-Length: 0
```

### Request body recorded for playback ():

```

```

### Response headers recorded for playback:

```
Content-Type: text/csv;charset=UTF-8
content-length: 136
connection: keep-alive
```

### Response body recorded for playback (200: text/csv;charset=UTF-8):

```
,d19e7b3cade2b87a5031c71855191637ea8835b1
A/,646f8a2439291fccbab6d9419ae0aa1b57a0d67b
hello.txt,f572d396fae9206628714fb2ce00f72e94f2258f
```

