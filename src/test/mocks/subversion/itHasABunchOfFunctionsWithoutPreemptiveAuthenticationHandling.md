## Interaction 0: PROPFIND /svn/dataset/A/AK/

## [Note] Test Context:

.merkle.csv does not work with no attempt to authenticate

### Request headers recorded for playback:

```
Connection: keep-alive
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
Host: localhost:8198
Accept-Encoding: gzip
Content-Length: 216
Depth: 1
Content-Type: text/xml; charset=UTF-8
```

### Request body recorded for playback (text/xml; charset=UTF-8):

```
<?xml version="1.0" encoding="utf-8" ?>
<D:propfind xmlns:D="DAV:">
<D:prop xmlns:S="http://subversion.tigris.org/xmlns/dav/">
<S:sha1-checksum/>
<D:version-name/>
<S:baseline-relative-path/>
</D:prop>
</D:propfind>

```

### Response headers recorded for playback:

```
Date: Wed, 01 Jan 2019 01:01:01 GMT
Server: Apache/2.4.39 (Unix)
WWW-Authenticate: Basic realm="Subversion Repository"
Content-Length: 456
Keep-Alive: timeout=5, max=100
Connection: Keep-Alive
Content-Type: text/html; charset=iso-8859-1
```

### Response body recorded for playback (401: text/html; charset=iso-8859-1):

```
<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML 2.0//EN">
<html><head>
<title>401 Unauthorized</title>
</head><body>
<h1>Unauthorized</h1>
<p>This server could not verify that you
are authorized to access the document
requested.  Either you supplied the wrong
credentials (e.g., bad password), or your
browser doesn't understand how to supply
the credentials required.</p>
<hr>
<address>Apache/2.4.39 (Unix) Server at localhost Port 8198</address>
</body></html>

```

## Interaction 1: PROPFIND /svn/dataset/A/AK/

## [Note] Test Context:

.merkle.csv works after idiomatic authentication challenge

### Request headers recorded for playback:

```
Connection: keep-alive
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
Host: localhost:8198
Accept-Encoding: gzip
Content-Length: 216
Depth: 1
Content-Type: text/xml; charset=UTF-8
```

### Request body recorded for playback (text/xml; charset=UTF-8):

```
<?xml version="1.0" encoding="utf-8" ?>
<D:propfind xmlns:D="DAV:">
<D:prop xmlns:S="http://subversion.tigris.org/xmlns/dav/">
<S:sha1-checksum/>
<D:version-name/>
<S:baseline-relative-path/>
</D:prop>
</D:propfind>

```

### Response headers recorded for playback:

```
Date: Wed, 01 Jan 2019 01:01:01 GMT
Server: Apache/2.4.39 (Unix)
WWW-Authenticate: Basic realm="Subversion Repository"
Content-Length: 456
Keep-Alive: timeout=5, max=99
Connection: Keep-Alive
Content-Type: text/html; charset=iso-8859-1
```

### Response body recorded for playback (401: text/html; charset=iso-8859-1):

```
<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML 2.0//EN">
<html><head>
<title>401 Unauthorized</title>
</head><body>
<h1>Unauthorized</h1>
<p>This server could not verify that you
are authorized to access the document
requested.  Either you supplied the wrong
credentials (e.g., bad password), or your
browser doesn't understand how to supply
the credentials required.</p>
<hr>
<address>Apache/2.4.39 (Unix) Server at localhost Port 8198</address>
</body></html>

```

## Interaction 2: PROPFIND /svn/dataset/A/AK/

### Request headers recorded for playback:

```
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
Host: localhost:8198
Accept-Encoding: gzip
Content-Length: 216
Depth: 1
Content-Type: text/xml; charset=UTF-8
```

### Request body recorded for playback (text/xml; charset=UTF-8):

```
<?xml version="1.0" encoding="utf-8" ?>
<D:propfind xmlns:D="DAV:">
<D:prop xmlns:S="http://subversion.tigris.org/xmlns/dav/">
<S:sha1-checksum/>
<D:version-name/>
<S:baseline-relative-path/>
</D:prop>
</D:propfind>

```

### Response headers recorded for playback:

```
Date: Wed, 01 Jan 2019 01:01:01 GMT
Server: Apache/2.4.39 (Unix)
Content-Length: 1529
Keep-Alive: timeout=5, max=98
Connection: Keep-Alive
Content-Type: text/xml; charset="utf-8"
```

### Response body recorded for playback (207: text/xml; charset="utf-8"):

```
<?xml version="1.0" encoding="utf-8"?>
<D:multistatus xmlns:D="DAV:" xmlns:ns1="http://subversion.tigris.org/xmlns/dav/" xmlns:ns0="DAV:">
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/" xmlns:g0="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/</D:href>
<D:propstat>
<D:prop>
<lp1:version-name>2</lp1:version-name>
<lp3:baseline-relative-path>A/AK</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
<D:propstat>
<D:prop>
<g0:sha1-checksum/>
</D:prop>
<D:status>HTTP/1.1 404 Not Found</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/" xmlns:g0="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/</D:href>
<D:propstat>
<D:prop>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
<D:propstat>
<D:prop>
<g0:sha1-checksum/>
</D:prop>
<D:status>HTTP/1.1 404 Not Found</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/hello.txt</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>f572d396fae9206628714fb2ce00f72e94f2258f</lp3:sha1-checksum>
<lp1:version-name>2</lp1:version-name>
<lp3:baseline-relative-path>A/AK/hello.txt</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
</D:multistatus>

```

## Interaction 3: OPTIONS /svn/dataset/A/AK/

### Request headers recorded for playback:

```
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
Host: localhost:8198
Accept-Encoding: gzip
Content-Length: 131
Content-Type: text/xml; charset=UTF-8
```

### Request body recorded for playback (text/xml; charset=UTF-8):

```
<?xml version="1.0" encoding="utf-8"?><D:options xmlns:D="DAV:"><D:activity-collection-set></D:activity-collection-set></D:options>
```

### Response headers recorded for playback:

```
Date: Wed, 01 Jan 2019 01:01:01 GMT
Server: Apache/2.4.39 (Unix)
DAV: 1,2
DAV: version-control,checkout,working-resource
DAV: merge,baseline,activity,version-controlled-collection
DAV: http://subversion.tigris.org/xmlns/dav/svn/depth
DAV: http://subversion.tigris.org/xmlns/dav/svn/log-revprops
DAV: http://subversion.tigris.org/xmlns/dav/svn/atomic-revprops
DAV: http://subversion.tigris.org/xmlns/dav/svn/partial-replay
DAV: http://subversion.tigris.org/xmlns/dav/svn/inherited-props
DAV: http://subversion.tigris.org/xmlns/dav/svn/inline-props
DAV: http://subversion.tigris.org/xmlns/dav/svn/reverse-file-revs
DAV: http://subversion.tigris.org/xmlns/dav/svn/mergeinfo
DAV: <http://apache.org/dav/propset/fs/1>
MS-Author-Via: DAV
Allow: OPTIONS,GET,HEAD,POST,DELETE,TRACE,PROPFIND,PROPPATCH,COPY,MOVE,LOCK,UNLOCK,CHECKOUT
DAV: http://subversion.tigris.org/xmlns/dav/svn/ephemeral-txnprops
SVN-Youngest-Rev: 2
SVN-Repository-UUID: d9ec1351-7e5d-4bf5-bac7-457d337bd02f
SVN-Repository-MergeInfo: yes
DAV: http://subversion.tigris.org/xmlns/dav/svn/replay-rev-resource
SVN-Repository-Root: /svn/dataset
SVN-Me-Resource: /svn/dataset/!svn/me
SVN-Rev-Root-Stub: /svn/dataset/!svn/rvr
SVN-Rev-Stub: /svn/dataset/!svn/rev
SVN-Txn-Root-Stub: /svn/dataset/!svn/txr
SVN-Txn-Stub: /svn/dataset/!svn/txn
SVN-VTxn-Root-Stub: /svn/dataset/!svn/vtxr
SVN-VTxn-Stub: /svn/dataset/!svn/vtxn
SVN-Allow-Bulk-Updates: On
SVN-Supported-Posts: create-txn
SVN-Supported-Posts: create-txn-with-props
Content-Length: 191
Keep-Alive: timeout=5, max=97
Connection: Keep-Alive
Content-Type: text/xml; charset="utf-8"
```

### Response body recorded for playback (200: text/xml; charset="utf-8"):

```
<?xml version="1.0" encoding="utf-8"?>
<D:options-response xmlns:D="DAV:">
<D:activity-collection-set><D:href>/svn/dataset/!svn/act/</D:href></D:activity-collection-set></D:options-response>

```

## Interaction 4: PROPFIND /svn/dataset/!svn/rvr/2/A/AK/

### Request headers recorded for playback:

```
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
Host: localhost:8198
Accept-Encoding: gzip
Content-Length: 100
Depth: 0
Content-Type: text/xml; charset=UTF-8
```

### Request body recorded for playback (text/xml; charset=UTF-8):

```
<?xml version="1.0" encoding="utf-8"?><propfind xmlns="DAV:"><prop><version-name/></prop></propfind>
```

### Response headers recorded for playback:

```
Date: Wed, 01 Jan 2019 01:01:01 GMT
Server: Apache/2.4.39 (Unix)
Content-Length: 369
Keep-Alive: timeout=5, max=96
Connection: Keep-Alive
Content-Type: text/xml; charset="utf-8"
```

### Response body recorded for playback (207: text/xml; charset="utf-8"):

```
<?xml version="1.0" encoding="utf-8"?>
<D:multistatus xmlns:D="DAV:" xmlns:ns0="DAV:">
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/!svn/rvr/2/A/AK/</D:href>
<D:propstat>
<D:prop>
<lp1:version-name>2</lp1:version-name>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
</D:multistatus>

```

## Interaction 5: PROPFIND /svn/dataset/A/AK/A/

### Request headers recorded for playback:

```
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
Host: localhost:8198
Accept-Encoding: gzip
Content-Length: 216
Depth: 1
Content-Type: text/xml; charset=UTF-8
```

### Request body recorded for playback (text/xml; charset=UTF-8):

```
<?xml version="1.0" encoding="utf-8" ?>
<D:propfind xmlns:D="DAV:">
<D:prop xmlns:S="http://subversion.tigris.org/xmlns/dav/">
<S:sha1-checksum/>
<D:version-name/>
<S:baseline-relative-path/>
</D:prop>
</D:propfind>

```

### Response headers recorded for playback:

```
Date: Wed, 01 Jan 2019 01:01:01 GMT
Server: Apache/2.4.39 (Unix)
Content-Length: 1132
Keep-Alive: timeout=5, max=95
Connection: Keep-Alive
Content-Type: text/xml; charset="utf-8"
```

### Response body recorded for playback (207: text/xml; charset="utf-8"):

```
<?xml version="1.0" encoding="utf-8"?>
<D:multistatus xmlns:D="DAV:" xmlns:ns1="http://subversion.tigris.org/xmlns/dav/" xmlns:ns0="DAV:">
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/" xmlns:g0="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/</D:href>
<D:propstat>
<D:prop>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
<D:propstat>
<D:prop>
<g0:sha1-checksum/>
</D:prop>
<D:status>HTTP/1.1 404 Not Found</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/" xmlns:g0="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/</D:href>
<D:propstat>
<D:prop>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
<D:propstat>
<D:prop>
<g0:sha1-checksum/>
</D:prop>
<D:status>HTTP/1.1 404 Not Found</D:status>
</D:propstat>
</D:response>
</D:multistatus>

```

## Interaction 6: OPTIONS /svn/dataset/A/AK/A/

### Request headers recorded for playback:

```
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
Host: localhost:8198
Accept-Encoding: gzip
Content-Length: 131
Content-Type: text/xml; charset=UTF-8
```

### Request body recorded for playback (text/xml; charset=UTF-8):

```
<?xml version="1.0" encoding="utf-8"?><D:options xmlns:D="DAV:"><D:activity-collection-set></D:activity-collection-set></D:options>
```

### Response headers recorded for playback:

```
Date: Wed, 01 Jan 2019 01:01:01 GMT
Server: Apache/2.4.39 (Unix)
DAV: 1,2
DAV: version-control,checkout,working-resource
DAV: merge,baseline,activity,version-controlled-collection
DAV: http://subversion.tigris.org/xmlns/dav/svn/depth
DAV: http://subversion.tigris.org/xmlns/dav/svn/log-revprops
DAV: http://subversion.tigris.org/xmlns/dav/svn/atomic-revprops
DAV: http://subversion.tigris.org/xmlns/dav/svn/partial-replay
DAV: http://subversion.tigris.org/xmlns/dav/svn/inherited-props
DAV: http://subversion.tigris.org/xmlns/dav/svn/inline-props
DAV: http://subversion.tigris.org/xmlns/dav/svn/reverse-file-revs
DAV: http://subversion.tigris.org/xmlns/dav/svn/mergeinfo
DAV: <http://apache.org/dav/propset/fs/1>
MS-Author-Via: DAV
Allow: OPTIONS,GET,HEAD,POST,DELETE,TRACE,PROPFIND,PROPPATCH,COPY,MOVE,LOCK,UNLOCK,CHECKOUT
DAV: http://subversion.tigris.org/xmlns/dav/svn/ephemeral-txnprops
SVN-Youngest-Rev: 2
SVN-Repository-UUID: d9ec1351-7e5d-4bf5-bac7-457d337bd02f
SVN-Repository-MergeInfo: yes
DAV: http://subversion.tigris.org/xmlns/dav/svn/replay-rev-resource
SVN-Repository-Root: /svn/dataset
SVN-Me-Resource: /svn/dataset/!svn/me
SVN-Rev-Root-Stub: /svn/dataset/!svn/rvr
SVN-Rev-Stub: /svn/dataset/!svn/rev
SVN-Txn-Root-Stub: /svn/dataset/!svn/txr
SVN-Txn-Stub: /svn/dataset/!svn/txn
SVN-VTxn-Root-Stub: /svn/dataset/!svn/vtxr
SVN-VTxn-Stub: /svn/dataset/!svn/vtxn
SVN-Allow-Bulk-Updates: On
SVN-Supported-Posts: create-txn
SVN-Supported-Posts: create-txn-with-props
Content-Length: 191
Keep-Alive: timeout=5, max=94
Connection: Keep-Alive
Content-Type: text/xml; charset="utf-8"
```

### Response body recorded for playback (200: text/xml; charset="utf-8"):

```
<?xml version="1.0" encoding="utf-8"?>
<D:options-response xmlns:D="DAV:">
<D:activity-collection-set><D:href>/svn/dataset/!svn/act/</D:href></D:activity-collection-set></D:options-response>

```

## Interaction 7: PROPFIND /svn/dataset/!svn/rvr/2/A/AK/A/

### Request headers recorded for playback:

```
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
Host: localhost:8198
Accept-Encoding: gzip
Content-Length: 100
Depth: 0
Content-Type: text/xml; charset=UTF-8
```

### Request body recorded for playback (text/xml; charset=UTF-8):

```
<?xml version="1.0" encoding="utf-8"?><propfind xmlns="DAV:"><prop><version-name/></prop></propfind>
```

### Response headers recorded for playback:

```
Date: Wed, 01 Jan 2019 01:01:01 GMT
Server: Apache/2.4.39 (Unix)
Content-Length: 371
Keep-Alive: timeout=5, max=93
Connection: Keep-Alive
Content-Type: text/xml; charset="utf-8"
```

### Response body recorded for playback (207: text/xml; charset="utf-8"):

```
<?xml version="1.0" encoding="utf-8"?>
<D:multistatus xmlns:D="DAV:" xmlns:ns0="DAV:">
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/!svn/rvr/2/A/AK/A/</D:href>
<D:propstat>
<D:prop>
<lp1:version-name>1</lp1:version-name>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
</D:multistatus>

```

## Interaction 8: PROPFIND /svn/dataset/A/AK/A/Alaska/

### Request headers recorded for playback:

```
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
Host: localhost:8198
Accept-Encoding: gzip
Content-Length: 216
Depth: 1
Content-Type: text/xml; charset=UTF-8
```

### Request body recorded for playback (text/xml; charset=UTF-8):

```
<?xml version="1.0" encoding="utf-8" ?>
<D:propfind xmlns:D="DAV:">
<D:prop xmlns:S="http://subversion.tigris.org/xmlns/dav/">
<S:sha1-checksum/>
<D:version-name/>
<S:baseline-relative-path/>
</D:prop>
</D:propfind>

```

### Response headers recorded for playback:

```
Date: Wed, 01 Jan 2019 01:01:01 GMT
Server: Apache/2.4.39 (Unix)
Keep-Alive: timeout=5, max=92
Connection: Keep-Alive
Transfer-Encoding: chunked
Content-Type: text/xml; charset="utf-8"
```

### Response body recorded for playback (207: text/xml; charset="utf-8"):

```
<?xml version="1.0" encoding="utf-8"?>
<D:multistatus xmlns:D="DAV:" xmlns:ns1="http://subversion.tigris.org/xmlns/dav/" xmlns:ns0="DAV:">
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/" xmlns:g0="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/</D:href>
<D:propstat>
<D:prop>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
<D:propstat>
<D:prop>
<g0:sha1-checksum/>
</D:prop>
<D:status>HTTP/1.1 404 Not Found</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2130.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>375fc259d26710afe31e1367a59de3d5dea729b1</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2130.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2122.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>e5247216a7a1f32851807d4b4b9cd1cf152cd57d</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2122.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2020.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>20e3ff1ade2385c593f73fd44fd157391d2424e7</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2020.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2220.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>594a20ec3d550eae2ad848fa8c0e08d50bd4e7fa</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2220.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2230.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>ec4c2c8dfc3cf4c1e6719de0544aaeba7731fc9c</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2230.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2150.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>b5c17ce941a9647fbc179cb1b769348cfdaebb98</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2150.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2050.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>19b2da433a273840deddb7a46b16891acab16e3f</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2050.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2240.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>798b13c1c4aa7ee2e5744f8b8a2b553c1447229b</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2240.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2170.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>c1d0842e2c77d53bee5c684e0e8ad580a2fff05f</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2170.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2164.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>ab2f71a47910463bdde92a77313f7e5edba00063</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2164.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2070.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>2d50d3100744fb7c4c5ead72a2896909fbe2ba6a</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2070.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2068.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>69576d3632c7ce8b0b2a42d87e9e75049bdaff9d</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2068.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2060.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>45418423999c155abc434e175d42ccf6534bee6d</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2060.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2261.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>0cd460d99cc7a871230cbe0f6f2ab703339e1630</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2261.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2270.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>4e77074d4adc2ac7bb486408b00bda26f98820ea</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2270.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2275.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>91ce27fc8b643540dda88afdc5b5b62d97a026fa</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2275.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2090.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>434e72590bdaa03176ebabc18e52dc0a24918da9</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2090.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2180.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>9759646b5a2811efc6bfaee84a2b813f85cb1e1a</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2180.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2185.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>e2beddc3f245ca79908d9a1590aa177151e66e4d</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2185.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2188.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>5881cdc3a15eac0cba15a3e07ad54176cabeeedf</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2188.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2195.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>b7085173d8685deecf70c6efb63d92ec6db2cf21</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2195.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2198.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>78a399df3eeb4f696812d6d46882e4029190cf67</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2198.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2282.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>3bec0f1f9e3ae818dd096303f796ce28e2fe6d08</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2282.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2290.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>fd4014808dd77351b939060f1283d395deb0cea5</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2290.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2110.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>c88ed14784907db94b11941b760892742bd043f3</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2110.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2105.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>f7bfd6be756c919e4ea69cb466f31ae0b2fd213d</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2105.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2100.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>a884cf405af5a20276b3b1cc72885833905ffa97</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2100.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2016.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>194f6519cd60b773a82857cf1aeba8dad4a223ed</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2016.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/A/AK/A/Alaska/2013.json</D:href>
<D:propstat>
<D:prop>
<lp3:sha1-checksum>1674790a70b984c9041ab86c370f942861ead004</lp3:sha1-checksum>
<lp1:version-name>1</lp1:version-name>
<lp3:baseline-relative-path>A/AK/A/Alaska/2013.json</lp3:baseline-relative-path>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
</D:multistatus>

```

## Interaction 9: OPTIONS /svn/dataset/A/AK/A/Alaska/

### Request headers recorded for playback:

```
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
Host: localhost:8198
Accept-Encoding: gzip
Content-Length: 131
Content-Type: text/xml; charset=UTF-8
```

### Request body recorded for playback (text/xml; charset=UTF-8):

```
<?xml version="1.0" encoding="utf-8"?><D:options xmlns:D="DAV:"><D:activity-collection-set></D:activity-collection-set></D:options>
```

### Response headers recorded for playback:

```
Date: Wed, 01 Jan 2019 01:01:01 GMT
Server: Apache/2.4.39 (Unix)
DAV: 1,2
DAV: version-control,checkout,working-resource
DAV: merge,baseline,activity,version-controlled-collection
DAV: http://subversion.tigris.org/xmlns/dav/svn/depth
DAV: http://subversion.tigris.org/xmlns/dav/svn/log-revprops
DAV: http://subversion.tigris.org/xmlns/dav/svn/atomic-revprops
DAV: http://subversion.tigris.org/xmlns/dav/svn/partial-replay
DAV: http://subversion.tigris.org/xmlns/dav/svn/inherited-props
DAV: http://subversion.tigris.org/xmlns/dav/svn/inline-props
DAV: http://subversion.tigris.org/xmlns/dav/svn/reverse-file-revs
DAV: http://subversion.tigris.org/xmlns/dav/svn/mergeinfo
DAV: <http://apache.org/dav/propset/fs/1>
MS-Author-Via: DAV
Allow: OPTIONS,GET,HEAD,POST,DELETE,TRACE,PROPFIND,PROPPATCH,COPY,MOVE,LOCK,UNLOCK,CHECKOUT
DAV: http://subversion.tigris.org/xmlns/dav/svn/ephemeral-txnprops
SVN-Youngest-Rev: 2
SVN-Repository-UUID: d9ec1351-7e5d-4bf5-bac7-457d337bd02f
SVN-Repository-MergeInfo: yes
DAV: http://subversion.tigris.org/xmlns/dav/svn/replay-rev-resource
SVN-Repository-Root: /svn/dataset
SVN-Me-Resource: /svn/dataset/!svn/me
SVN-Rev-Root-Stub: /svn/dataset/!svn/rvr
SVN-Rev-Stub: /svn/dataset/!svn/rev
SVN-Txn-Root-Stub: /svn/dataset/!svn/txr
SVN-Txn-Stub: /svn/dataset/!svn/txn
SVN-VTxn-Root-Stub: /svn/dataset/!svn/vtxr
SVN-VTxn-Stub: /svn/dataset/!svn/vtxn
SVN-Allow-Bulk-Updates: On
SVN-Supported-Posts: create-txn
SVN-Supported-Posts: create-txn-with-props
Content-Length: 191
Keep-Alive: timeout=5, max=91
Connection: Keep-Alive
Content-Type: text/xml; charset="utf-8"
```

### Response body recorded for playback (200: text/xml; charset="utf-8"):

```
<?xml version="1.0" encoding="utf-8"?>
<D:options-response xmlns:D="DAV:">
<D:activity-collection-set><D:href>/svn/dataset/!svn/act/</D:href></D:activity-collection-set></D:options-response>

```

## Interaction 10: PROPFIND /svn/dataset/!svn/rvr/2/A/AK/A/Alaska/

### Request headers recorded for playback:

```
Authorization: Basic aGFycnk6aGFycnlwdw==
Connection: keep-alive
User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9
Host: localhost:8198
Accept-Encoding: gzip
Content-Length: 100
Depth: 0
Content-Type: text/xml; charset=UTF-8
```

### Request body recorded for playback (text/xml; charset=UTF-8):

```
<?xml version="1.0" encoding="utf-8"?><propfind xmlns="DAV:"><prop><version-name/></prop></propfind>
```

### Response headers recorded for playback:

```
Date: Wed, 01 Jan 2019 01:01:01 GMT
Server: Apache/2.4.39 (Unix)
Content-Length: 378
Keep-Alive: timeout=5, max=90
Connection: Keep-Alive
Content-Type: text/xml; charset="utf-8"
```

### Response body recorded for playback (207: text/xml; charset="utf-8"):

```
<?xml version="1.0" encoding="utf-8"?>
<D:multistatus xmlns:D="DAV:" xmlns:ns0="DAV:">
<D:response xmlns:lp1="DAV:" xmlns:lp3="http://subversion.tigris.org/xmlns/dav/">
<D:href>/svn/dataset/!svn/rvr/2/A/AK/A/Alaska/</D:href>
<D:propstat>
<D:prop>
<lp1:version-name>1</lp1:version-name>
</D:prop>
<D:status>HTTP/1.1 200 OK</D:status>
</D:propstat>
</D:response>
</D:multistatus>

```

