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

    protected void html(Request req, Response rsp) throws Throwable {
        doJoobyResponse(rsp, svnMerkleizer.doDirectoryList(
                dir -> SvnMerkleizer.toHtml(dir), "text/html", req.path(), getAuthorization(req)
        ));
    }

    protected void xml(Request req, Response rsp) throws Throwable {
        doJoobyResponse(rsp, svnMerkleizer.doDirectoryList(
                dir -> SvnMerkleizer.toXML(dir), "text/xml", req.path(), getAuthorization(req)
        ));
    }

    protected void txt(Request req, Response rsp) throws Throwable {
        doJoobyResponse(rsp, svnMerkleizer.doDirectoryList(
                dir -> dir.sha1 + "\n" + svnMerkleizer.toText(dir.contents), "text/plain", req.path(), getAuthorization(req)
        ));
    }


    protected void csv(Request req, Response rsp) throws Throwable {
        doJoobyResponse(rsp, svnMerkleizer.doDirectoryList(
                dir -> "," + dir.sha1 + "\n" + SvnMerkleizer.toCSV(dir.contents), "text/csv", req.path(), getAuthorization(req)
        ));
    }

    protected void json(Request req, Response rsp) throws Throwable {
        doJoobyResponse(rsp, svnMerkleizer.doDirectoryList(
                dir -> svnMerkleizer.toPrettyJson(dir), "application/json", req.path(), getAuthorization(req)
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

    public static class ViaHiddenGetRoutes extends SubversionDirectoryMerkleizerService {

        public ViaHiddenGetRoutes(String delegateToUrl, String contextDir, SvnMerkleizer.Metrics metrics,
                                  SvnMerkleizer svnMerkleizer, int port) {
            super(svnMerkleizer);

            //        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);

            path(contextDir, () -> {
                port(port);
//                before("**/*", (req, rsp) -> {
//                    String p = req.path();
//                    System.out.println(p);
//                });
                get("**/.merkle.json", (req, rsp) -> {
                    json(req, rsp);
                });

                get("**/.merkle.csv", (req, rsp) -> {
                    csv(req, rsp);
                });

                get("**/.merkle.txt", (req, rsp) -> {
                    txt(req, rsp);
                });

                get("**/.merkle.xml", (req, rsp) -> {
                    xml(req, rsp);
                });

                get("**/.merkle.html", (req, rsp) -> {
                    html(req, rsp);
                });
            });
        }
    }

}
