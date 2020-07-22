/*
        SvnMerkleizer: Adds a Merkle Tree to Subversion

        Copyright (c) 2017-2019, Paul Hammant
        All rights reserved.

        Redistribution and use in source and binary forms, with or without
        modification, are permitted provided that the following conditions are met:

        1. Redistributions of source code must retain the above copyright notice, this
        list of conditions and the following disclaimer.
        2. Redistributions in binary form must reproduce the above copyright notice,
        this list of conditions and the following disclaimer in the documentation
        and/or other materials provided with the distribution.

        THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
        ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
        WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
        DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
        ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
        (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
        LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
        ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
        (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
        SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

        The views and conclusions contained in the software and documentation are those
        of the authors and should not be interpreted as representing official policies,
        either expressed or implied, of the Servirtium project.
*/
package com.paulhammant.svnmerkleizer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.paulhammant.svnmerkleizer.pojos.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class SvnMerkleizer {

    public static final HashFunction SHA_1 = Hashing.sha1();
    public static final int LENGTH_HTTP_INTRODUCER = "http://x".length();

    private final String delegateToUrl;
    private final String contextDir;
    private final ConcurrentMap<String, VersionInfo> cache;
    private final Metrics metrics;
    private final static ObjectMapper JACKSON_OBJECT_MAPPER = new ObjectMapper();
    private DB mapDBcache;
    private String cacheFilePath;
    private final OkHttpClient okHttpClient;

    static {
        JACKSON_OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JACKSON_OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, Directory.class);
    }

    public SvnMerkleizer(String delegateToUrl, String contextDir,
                         Metrics metrics, String cacheFilePath,
                         final OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        this.delegateToUrl = delegateToUrl;
        this.contextDir = contextDir;
        if (cacheFilePath != null && !cacheFilePath.equals("")) {
            this.cacheFilePath = cacheFilePath;
        } else {
            this.cacheFilePath = "merkleizer.db";
        }
        this.cache = initializeDB();
        this.metrics = metrics;

    }


    protected ConcurrentMap<String, VersionInfo> initializeDB() {

        mapDBcache = DBMaker.fileDB(cacheFilePath).make();
        ConcurrentMap<String, VersionInfo> cache = mapDBcache.hashMap("merkle-cache", Serializer.STRING, Serializer.JAVA).createOrOpen();
        return cache;

    }

    public void close() {
        mapDBcache.close();
    }

    public void clearCache() {
        cache.clear();
    }

    public interface Metrics {
        void doDirectoryList(long durationMillis, Counts counts);

        class NullObject implements Metrics {
            @Override
            public void doDirectoryList(long durationMillis, Counts counts) {
            }
        }

        class Console implements Metrics {
            @Override
            public void doDirectoryList(long durationMillis, Counts counts) {
                System.out.println("Duration=" +  durationMillis + "ms, PROPFIND count=" + counts.propfind + ", OPTIONS count=" + counts.options);
            }
        }
    }

    public static class Counts {
        public int propfind;
        public int options;
    }

    public class SvnMerkelizerResponse {

        private int responseCode;
        private Map<String, String> headers = new HashMap<>();
        private String text;
        private String contentType;

        public void status(int responseCode) {

            this.responseCode = responseCode;
        }

        public void header(String key, String val) {
            headers.put(key, val);
        }

        public void send(String text) {
            this.text = text;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public String getText() {
            return text;
        }

        public String getContentType() {
            return contentType;
        }

        public void type(String contentType) {
            this.contentType = contentType;
        }
    }

    public SvnMerkelizerResponse doDirectoryList(XStream svnXmlConverter,
                                                 Output op, String contentType, String path, String authorization)
            throws IOException, NotFound404 {
        long start = System.currentTimeMillis();
        String url = destinationUrl(path, delegateToUrl, contextDir);
        String serverPart = url.substring(0, url.indexOf("/", LENGTH_HTTP_INTRODUCER));
        String pathPart = url.substring(url.indexOf("/", LENGTH_HTTP_INTRODUCER));
        Counts counts = new Counts();
        Items items = getItems(getUser(authorization), svnXmlConverter,
                cache, serverPart, pathPart, authorization, false, counts);
        SvnMerkelizerResponse resp = new SvnMerkelizerResponse();
        if (items.dir == null) {
            resp.status(items.responseCode);
            if (items.responseCode == 401) {
                resp.header("WWW-Authenticate", "Basic realm=\"Subversion Repository\"");
            }
            return resp;
        }
        resp.type(contentType);
        resp.send(op.render(items.dir));
        metrics.doDirectoryList((System.currentTimeMillis() - start), counts);
        return resp;
    }

    static String toCSV(List<Entry> entries) {
        String[] stringified = new String[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            String name;
            if (entry.file == null) {
                name = entry.dir + "/";
            } else {
                name = entry.file;
            }
            if (name.contains(",")) {
                name = "\"" + name + "\"";
            }
            stringified[i] = name + "," + entry.sha1;
        }
        Arrays.sort(stringified);
        return String.join("\n", stringified);
    }

    static String toHtml(Directory dir) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>\n");
        sb.append("<p>").append(dir.sha1).append("</p>\n");
        sb.append("<table>\n");
        for (int i = 0; i < dir.contents.size(); i++) {
            Entry entry = dir.contents.get(i);
            sb.append("<tr><td>");
            if (entry.dir != null) {
                sb.append("<a href=\"").append(entry.dir).append("/.merkle.html\">").append(entry.dir).append("</a>");
            } else {
                sb.append(entry.file);
            }
            sb.append("</td><td>").append(entry.sha1);
            sb.append("</td></tr>\n");
        }

        sb.append("</table></body></html>");
        return sb.toString();
    }

    /**
     * The toTXT operation is important for SHA1 calculation as it
     * can be replicated on the client side quite easily.
     */
    static String toText(List<Entry> entries) {
        String[] stringified = new String[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            String name;
            if (entry.file == null) {
                name = entry.dir + "/";
            } else {
                name = entry.file;
            }
            stringified[i] = name + " " + entry.sha1;
        }
        Arrays.sort(stringified);
        return String.join("\n", stringified);
    }

    static String toPrettyJson(Directory dir) {
        try {
            return JACKSON_OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(dir);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    private Items getItems(String user,
                           XStream svnXmlConverter,
                           ConcurrentMap<String, VersionInfo> cache,
                           String delegateToUrl, String pathPart,
                           String authorization, boolean sha1Only, Counts counts) throws IOException, NotFound404 {

        final okhttp3.Response response = propfindDirlist(authorization, delegateToUrl + pathPart, counts);
        if (response.code() < 200 || response.code() > 299) {
            response.close();
            return Items.notSuccessful(response.code());
        }

        String xml = eliminateNamespaces(response.body().string());
        final int xmlHashCode = xml.hashCode();

        if (xml.contains("\nCould not find the requested SVN filesystem\n")
                || xml.contains("\n<title>405 Method Not Allowed</title>\n")) {
            throw new NotFound404();
        }

        final PropfindSvnResult multiStatus;
        try {
            multiStatus = (PropfindSvnResult) svnXmlConverter.fromXML(xml);
        } catch (XStreamException e) {
            throw new UnsupportedOperationException("XStream could not deserialize URL " + delegateToUrl + pathPart + " payload: " + xml, e);

        }
        final String baselineRelativePath = pluckBaselineRelativePath(multiStatus);

        Directory directory = flattenToItems(multiStatus);

        String svnRoot = pathPart.substring(0, pathPart.length() - baselineRelativePath.length() - 1);
        if (!svnRoot.endsWith("/")) {
            svnRoot += "/";
        }

        int currSvnRevision = -1;

        final String cacheKey = user + ":" + pathPart;
        if (sha1Only) {
            final VersionInfo versionInfo = (VersionInfo) cache.get(cacheKey);
            if (versionInfo != null) {
                if (versionInfo.xmlHashCode == xmlHashCode) {
                    // costly, but can't be avoided before doing the cache lookup.
                    currSvnRevision = getSvnRevision(delegateToUrl, pathPart, authorization, svnXmlConverter, svnRoot, counts);
                    if (versionInfo.svnRevision == currSvnRevision) {
                        cacheHit(cacheKey);
                        return Items.hereItIs(Directory.versionInfoAndSha1Only(versionInfo));
                    }
                }
            }
            cacheMiss(cacheKey);
        }

        if (currSvnRevision == -1) {
            currSvnRevision = getSvnRevision(delegateToUrl, pathPart, authorization, svnXmlConverter, svnRoot, counts);
        }

        // Recursion into directories here (potentially very costly)
        for (int i = 0; i < directory.contents.size(); i++) {
            Entry entry = directory.contents.get(i);
            if (entry.isDirWithNoSha1()) {
                Items items = getItems(user, svnXmlConverter, cache, delegateToUrl, pathPart + entry.dir + "/", authorization, true, counts);
                if (items.dir == null) {
                    return items;
                }
                entry.versionInfo = items.dir.versionInfo;
                entry.sha1 = entry.versionInfo.sha1; // for use in Jackson or XStream serialization only
            }
        }

        makeVersionInfoForPath(xmlHashCode, directory, currSvnRevision, pathPart);

        cache.put(cacheKey, directory.versionInfo);
        return Items.hereItIs(directory);
    }

    public void cacheMiss(String cacheKey) {
    }

    public void cacheHit(String cacheKey) {
    }

    protected String destinationUrl(String path, String delegateToUrl, String contextDir) {
        if (contextDir.equals("")) {
            path = delegateToUrl + path;
        } else {
            path = path.replace("/" + contextDir + "/", delegateToUrl);
        }
        return path.substring(0, path.lastIndexOf('/')) + "/";
    }

    private String getUser(String authorization) {
        if (authorization == null) {
            return "*anon*";
        }
        return new String(Base64.getDecoder().decode(authorization.substring(6))).split(":")[0];
    }

    private String pluckBaselineRelativePath(PropfindSvnResult result) {
        for (int i = 0; i < result.responses.size(); i++) {
            DResponse dResponse = result.responses.get(i);
            for (int j = 0; j < dResponse.propstats.size(); j++) {
                DPropstat dPropstat = dResponse.propstats.get(j);
                if (dPropstat.prop.baselineRelativePath != null) {
                    return dPropstat.prop.baselineRelativePath;
                }
            }
        }
        throw new RuntimeException("No baseline relative path found");
    }

    private int pluckVersion(PropfindSvnResult result) {
        // TODO lambda equiv
        for (int i = 0; i < result.responses.size(); i++) {
            DResponse dResponse = result.responses.get(i);
            for (int j = 0; j < dResponse.propstats.size(); j++) {
                DPropstat dPropstat = dResponse.propstats.get(j);
                if (dPropstat.prop.versionName != null) {
                    return Integer.parseInt(dPropstat.prop.versionName);
                }
            }
        }
        throw new RuntimeException("No version number found");
    }

    private int getSvnRevision(String delegateToUrl, String pathPart, String authorization, XStream svnXmlConverter, String svnRoot, Counts counts) throws IOException {

        String youngestRev = options(authorization, delegateToUrl + pathPart, counts);
        String url = (delegateToUrl + svnRoot + "!svn/rvr/" + youngestRev + "/" + pathPart.substring(svnRoot.length())).replace("//","/");
        okhttp3.Response response = propfindItemOnly(authorization, url, counts);
        String string = response.body().string();
        PropfindSvnResult result = null;
        result = (PropfindSvnResult) svnXmlConverter.fromXML(eliminateNamespaces(string));
        if (response.code() != 207) {
            throw new RuntimeException("wrong status code " + response.code());
        }
        return pluckVersion(result);
    }

    private okhttp3.Response propfindDirlist(String authorization, String url,
                                             Counts counts) throws IOException {

        String data = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
                "<D:propfind xmlns:D=\"DAV:\">\n" +
                "<D:prop xmlns:S=\"http://subversion.tigris.org/xmlns/dav/\">\n" +
                "<S:sha1-checksum/>\n" +
                "<D:version-name/>\n" +
                "<S:baseline-relative-path/>\n" +
                "</D:prop>\n" +
                "</D:propfind>\n";

        Map<String, String> headers = new HashMap<>();
        headers.put("Depth", "1");
        return getResponse(authorization, url, counts, data, headers);
    }

    private okhttp3.Response getResponse(String authorization, String url, Counts counts, String data, Map<String, String> headers) throws IOException {
        if (authorization != null) {
            headers.put("Authorization", authorization);
        }
        okhttp3.Response response = okHttpClient.newCall(new okhttp3.Request.Builder()
                .url(url)
                .method("PROPFIND", RequestBody.create(MediaType.parse("text/xml"), data))
                .headers(Headers.of(headers))
                .build()).execute();
        counts.propfind++;
        return response;
    }

    private okhttp3.Response propfindItemOnly(String auth, String url, Counts counts) throws IOException {

        String data = "<?xml version=\"1.0\" encoding=\"utf-8\"?><propfind xmlns=\"DAV:\"><prop><version-name/></prop></propfind>";

        Map<String, String> headers = new HashMap<>();
        headers.put("Depth", "0");
        return getResponse(auth, url, counts, data, headers);
    }

    private String options(String auth, String url, Counts counts) throws IOException {

        String data = "<?xml version=\"1.0\" encoding=\"utf-8\"?><D:options xmlns:D=\"DAV:\"><D:activity-collection-set></D:activity-collection-set></D:options>";

        Map<String, String> headers = new HashMap<>();
        if (auth != null) {
            headers.put("Authorization", auth);
        }

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .method("OPTIONS", RequestBody.create(MediaType.parse("text/xml"), data))
                .headers(Headers.of(headers))
                .build();
        okhttp3.Response response = okHttpClient.newCall(request).execute();
        counts.options++;
        String youngestRev = response.header("SVN-Youngest-Rev");
        response.body().close();
        return youngestRev;
    }

    static String eliminateNamespaces(String resp) {
        for (String repl : Arrays.asList("D:", "lp1:", "lp2:", "lp3:", "g0:")) {
            resp = resp.replace("<" + repl, "<");
            resp = resp.replace("</" + repl, "</");
        }
        return resp;
    }

    private String calcSHA1forItems(List<Entry> entries) {
        return SHA_1.hashString(toText(entries), Charsets.UTF_8).toString().intern();
    }

    static Directory flattenToItems(PropfindSvnResult multiStatus) {
        Directory directory = new Directory();
        directory.contents = new ArrayList<>();
        String cd = null;
        for (int i = 0; i < multiStatus.responses.size(); i++) {
            DResponse response = multiStatus.responses.get(i);
            String sha1 = null;
            String file = null;
            boolean isDir = response.href.endsWith("/");
            for (int j = 0; j < response.propstats.size(); j++) {
                DPropstat propstat = response.propstats.get(j);
                if (cd == null) {
                    cd = propstat.prop.baselineRelativePath;
                }
                if (propstat.prop.baselineRelativePath != null) {
                    file = Paths.get(propstat.prop.baselineRelativePath).getFileName().toString();
                }
                if (propstat.prop.sha1Checksum != null) {
                    sha1 = propstat.prop.sha1Checksum;
                }
            }
            if (sha1 == null) {
                sha1 = "unknown";
            }
            if (file == null || "".equals(file)) {
                continue;
            }
            if (i == 0) {
                continue;
            }
            if (isDir) {
                directory.contents.add(Entry.dir(file, sha1));
            } else {
                directory.contents.add(Entry.file(file, sha1));
            }
        }
        return directory;
    }


    void makeVersionInfoForPath(int xmlHashCode, Directory directory, int rvn, String pathPart) {
        directory.versionInfo = new VersionInfo();
        directory.versionInfo.svnRevision = rvn;
        directory.versionInfo.sha1 = calcSHA1forItems(directory.contents);
        directory.versionInfo.xmlHashCode = xmlHashCode;
        directory.sha1 = directory.versionInfo.sha1; // for serialization
    }

    static class Items {
        Directory dir;
        int responseCode;

        static Items notSuccessful(int code) {
            Items i = new Items();
            i.responseCode = code;
            return i;
        }

        static Items hereItIs(Directory directory) {
            Items i = new Items();
            i.dir = directory;
            return i;
        }
    }

    public interface Output {
        String render(Directory dir);
    }

    private class NotFound404 extends Exception {

    }
}