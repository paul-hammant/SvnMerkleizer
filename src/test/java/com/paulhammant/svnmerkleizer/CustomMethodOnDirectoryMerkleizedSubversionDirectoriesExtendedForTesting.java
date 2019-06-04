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

import com.thoughtworks.xstream.XStream;
import okhttp3.OkHttpClient;
import org.jooby.Request;
import org.jooby.RequestLogger;
import org.jooby.Response;

public class CustomMethodOnDirectoryMerkleizedSubversionDirectoriesExtendedForTesting
        extends SubversionDirectoryMerkleizerService.ViaCustomMethodOnDirectory
        implements TestingSubversionDirectoryMerkleizerService {

    private boolean started;
    private boolean stopped;

    public CustomMethodOnDirectoryMerkleizedSubversionDirectoriesExtendedForTesting() {
        super("TESTING-CUSTOM-METHOD",
                "http://localhost:8098/svn/dataset/",
                "merkle", "merkleizer.db", new SvnMerkleizer.Metrics.Console(), 8080);
        use("*", new RequestLogger().log(line -> {
            // nothing so far;
        }));
        onStarted(() -> {
            started = true;
        });
        onStop(() -> {
            stopped = true;
        });
    }

    @Override
    protected void html(OkHttpClient okHttpClient, XStream svnXmlConverter, Request req, Response rsp) throws Throwable {
        rsp.type("text/plain"); // not important fot test
        rsp.send("html()");
    }

    @Override
    protected void xml(OkHttpClient okHttpClient, XStream svnXmlConverter, XStream directoryXmlSerializer, Request req, Response rsp) throws Throwable {
        rsp.type("text/plain"); // not important fot test
        try {
            rsp.send("xml()");
        } catch (NullPointerException throwable) {
            System.out.println(throwable.getMessage());

        }
    }

    @Override
    protected void txt(OkHttpClient okHttpClient, XStream svnXmlConverter, Request req, Response rsp) throws Throwable {
        rsp.type("text/plain"); // not important fot test
        rsp.send("txt()");
    }

    @Override
    protected void csv(OkHttpClient okHttpClient, XStream svnXmlConverter, Request req, Response rsp) throws Throwable {
        rsp.type("text/plain"); // not important fot test
        rsp.send("csv()");
    }

    @Override
    protected void json(OkHttpClient okHttpClient, XStream svnXmlConverter, Request req, Response rsp) throws Throwable {
        rsp.type("text/plain"); // not important fot test
        rsp.send("json()");
    }

    @Override
    public Object getJournal() {
        return null;
    }

    @Override
    public boolean appStarted() {
        return started;
    }

    @Override
    public boolean appStopped() {
        return stopped;
    }

    @Override
    public String getRequestLog() {
        return "";
    }

    @Override
    public void setCacheItemsToRev1AsIfThatWereRealityInTheSvnRepo() {

    }

    @Override
    public void wipeJournal() {
    }

    @Override
    public void deleteCacheKeys() {
    }

    @Override
    public void writeSummaryToJournal() {
    }

}
