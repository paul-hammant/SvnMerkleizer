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

import com.paulhammant.svnmerkleizer.pojos.DProp;
import com.paulhammant.svnmerkleizer.pojos.DPropstat;
import com.paulhammant.svnmerkleizer.pojos.DResponse;
import com.paulhammant.svnmerkleizer.pojos.Directory;
import com.paulhammant.svnmerkleizer.pojos.Entry;
import com.paulhammant.svnmerkleizer.pojos.PropfindSvnResult;
import com.thoughtworks.xstream.XStream;
import okhttp3.OkHttpClient;
import org.jooby.Jooby;
import org.jooby.Mutant;
import org.jooby.Request;
import org.jooby.Response;

import static com.paulhammant.svnmerkleizer.NullNettyInternalLoggerFactory.noLoggingForNettyPlease;

/**
 * @author Paul Hammant
 */
public abstract class SubversionDirectoryMerkleizerService extends Jooby {

    private SvnMerkleizer svnMerkleizer;

    static {
        noLoggingForNettyPlease();
    }

    public SubversionDirectoryMerkleizerService(SvnMerkleizer svnMerkleizer) {
        this.svnMerkleizer = svnMerkleizer;

        /* Jooby's own 404 spits out stack trace, which can be silenced this way */
        err(404, (req, rsp, err) -> {
            rsp.status(404);
            rsp.send("");
            rsp.end();
        });

    }

    private static String getAuthorization(Request req) {
        Mutant authorization = req.headers().get("Authorization");
        if (authorization != null) {
            return authorization.value();
        } else {
            return null;
        }
    }

    @Override
    public void stop() {
        svnMerkleizer.close();
        super.stop();
    }

    protected void html(OkHttpClient okHttpClient, XStream svnXmlConverter, Request req, Response rsp) throws Throwable {
        doJoobyResponse(rsp, svnMerkleizer.doDirectoryList(okHttpClient, svnXmlConverter,
                dir -> SvnMerkleizer.toHtml(dir), "text/html", req.path(), getAuthorization(req)
        ));
    }

    protected void xml(OkHttpClient okHttpClient, XStream svnXmlConverter, XStream directoryXmlSerializer, Request req, Response rsp) throws Throwable {
        doJoobyResponse(rsp, svnMerkleizer.doDirectoryList(okHttpClient, svnXmlConverter,
                dir -> directoryXmlSerializer.toXML(dir), "text/xml", req.path(), getAuthorization(req)
        ));
    }

    protected void txt(OkHttpClient okHttpClient, XStream svnXmlConverter, Request req, Response rsp) throws Throwable {
        doJoobyResponse(rsp, svnMerkleizer.doDirectoryList(okHttpClient, svnXmlConverter,
                dir -> dir.sha1 + "\n" + svnMerkleizer.toTXT(dir.contents), "text/plain", req.path(), getAuthorization(req)
        ));
    }


    protected void csv(OkHttpClient okHttpClient, XStream svnXmlConverter, Request req, Response rsp) throws Throwable {
        doJoobyResponse(rsp, svnMerkleizer.doDirectoryList(okHttpClient, svnXmlConverter,
                dir -> "," + dir.sha1 + "\n" + SvnMerkleizer.toCSV(dir.contents), "text/csv", req.path(), getAuthorization(req)
        ));
    }

    protected void json(OkHttpClient okHttpClient, XStream svnXmlConverter, Request req, Response rsp) throws Throwable {
        doJoobyResponse(rsp, svnMerkleizer.doDirectoryList(okHttpClient, svnXmlConverter,
                dir -> svnMerkleizer.writePrettyJson(dir), "application/json", req.path(), getAuthorization(req)
        ));
    }

    private void doJoobyResponse(Response rsp, SvnMerkleizer.SvnMerkelizerResponse resp) throws Throwable {
        if (resp.getResponseCode() != 0) {
            rsp.status(resp.getResponseCode());
        }
        if (resp.getHeaders().size() > 0) {
            for (String k : resp.getHeaders().keySet()) {
                rsp.header(k, resp.getHeaders().get(k));
            }
        }
        if (resp.getContentType() != null) {
            rsp.type(resp.getContentType());
        }
        if (resp.getText() != null) {
            rsp.send(resp.getText());
        } else {
            rsp.end();
        }
    }

    public void deleteCacheKeys() {
        svnMerkleizer.clearCache();
    }

    static XStream makePropfindXmlConverter() {
        XStream svnXmlConverter = new XStream();
        svnXmlConverter.allowTypesByWildcard(new String[] {
                "com.paulhammant.svnmerkleizer.pojos.**"
        });
        svnXmlConverter.processAnnotations(new Class[]{PropfindSvnResult.class, DResponse.class, DProp.class, DPropstat.class});
        return svnXmlConverter;
    }

    static XStream makeDirectoryXmlSerializer() {
        XStream svnXmlConverter = new XStream();
        svnXmlConverter.allowTypesByWildcard(new String[] {
                "com.paulhammant.svnmerkleizer.pojos.**"
        });
        svnXmlConverter.processAnnotations(new Class[]{Directory.class, Entry.class});
        return svnXmlConverter;
    }

    public static class ViaCustomMethodOnDirectory extends SubversionDirectoryMerkleizerService {

        public ViaCustomMethodOnDirectory(String method, String delegateToUrl, String contextDir, String cacheFilePath, SvnMerkleizer.Metrics metrics, int port) {
            super(new SvnMerkleizer(delegateToUrl, contextDir, metrics, cacheFilePath));

            OkHttpClient okHttpClient = new OkHttpClient();
            XStream svnXmlConverter = makePropfindXmlConverter();
            XStream directoryXmlSerializer = makeDirectoryXmlSerializer();

            use(method, "**/", (req, rsp) -> {
                port(port);
                Mutant expectsTypeHdr = req.header("expects-type");
                expectsTypeHeaderShouldBeSetCheck(rsp, expectsTypeHdr);
                if (rsp.status().isPresent()) return;
                // Delete this block when https://github.com/jooby-project/jooby/issues/967 is fixed
                onlyForDirectriesCheck(method, req, rsp);
                if (rsp.status().isPresent()) return;

                String expectsType = expectsTypeHdr.value();
                switch (expectsType) {
                    case "JSON": json(okHttpClient, svnXmlConverter, req, rsp); break;
                    case "CSV": csv(okHttpClient, svnXmlConverter, req, rsp); break;
                    case "TXT": txt(okHttpClient, svnXmlConverter, req, rsp); break;
                    case "XML": xml(okHttpClient, svnXmlConverter, directoryXmlSerializer, req, rsp); break;
                    case "HTML": html(okHttpClient, svnXmlConverter, req, rsp); break;
                    default:
                        rsp.status(500);
                        rsp.type("text/plain");
                        rsp.send("Specified 'expects-type' header of '" + expectsType + "' isn't supported. JSON, CSV, TXT, XML, HTML are the allowed ones");
                }
            });
        }

        private void onlyForDirectriesCheck(String method, Request req, Response rsp) throws Throwable {
            if (!(req.rawPath().endsWith("/"))) {
                rsp.status(500);
                rsp.send("You can only do " + method + " operations on directories");
            }
        }

        private void expectsTypeHeaderShouldBeSetCheck(Response rsp, Mutant expectsHdr) throws Throwable {
            if (!(expectsHdr.isSet())) {
                rsp.status(500);
                rsp.send("The 'expects-type' header should have been set");
            }
        }

    }

    public static class ViaHiddenGetRoutes extends SubversionDirectoryMerkleizerService {

        public ViaHiddenGetRoutes(String delegateToUrl, String contextDir, SvnMerkleizer.Metrics metrics, SvnMerkleizer svnMerkleizer, int port) {
            super(svnMerkleizer);

            OkHttpClient okHttpClient = new OkHttpClient();
//        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
            XStream svnXmlConverter = makePropfindXmlConverter();
            XStream directoryXmlSerializer = makeDirectoryXmlSerializer();

            path(contextDir, () -> {
                port(port);
//                before("**/*", (req, rsp) -> {
//                    String p = req.path();
//                    System.out.println(p);
//                });
                get("**/.merkle.json", (req, rsp) -> {
                    json(okHttpClient, svnXmlConverter, req, rsp);
                });

                get("**/.merkle.csv", (req, rsp) -> {
                    csv(okHttpClient, svnXmlConverter, req, rsp);
                });

                get("**/.merkle.txt", (req, rsp) -> {
                    txt(okHttpClient, svnXmlConverter, req, rsp);
                });

                get("**/.merkle.xml", (req, rsp) -> {
                    xml(okHttpClient, svnXmlConverter, directoryXmlSerializer, req, rsp);
                });

                get("**/.merkle.html", (req, rsp) -> {
                    html(okHttpClient, svnXmlConverter, req, rsp);
                });
            });
        }
    }

}
