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

import com.paulhammant.servirtium.*;
import com.paulhammant.servirtium.jetty.JettyServirtiumServer;
import com.paulhammant.servirtium.svn.SubversionInteractionManipulations;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertEquals;

public class Testttt {

    public static final String servirtiumConversation = "## Interaction 0: GET /abc123/A/AK/.merkle.csv\n" +
            "\n" +
            "### Request headers recorded for playback:\n" +
            "\n" +
            "```\n" +
            "Authorization: Basic aGFycnk6aGFycnlwdw==\n" +
            "Accept: */*\n" +
            "Connection: keep-alive\n" +
            "User-Agent: SVN/1.10.0 (x86_64-apple-darwin17.0.0) serf/1.3.9\n" +
            "Host: localhost:8080\n" +
            "Accept-Encoding: gzip,deflate\n" +
            "Content-Length: 0\n" +
            "```\n" +
            "\n" +
            "### Request body recorded for playback ():\n" +
            "\n" +
            "```\n" +
            "\n" +
            "```\n" +
            "\n" +
            "### Response headers recorded for playback:\n" +
            "\n" +
            "```\n" +
            "Content-Type: text/csv;charset=UTF-8\n" +
            "content-length: 136\n" +
            "connection: keep-alive\n" +
            "```\n" +
            "\n" +
            "### Response body recorded for playback (200: text/csv;charset=UTF-8):\n" +
            "\n" +
            "```\n" +
            ",d19e7b3cade2b87a5031c71855191637ea8835b1\n" +
            "A/,646f8a2439291fccbab6d9419ae0aa1b57a0d67b\n" +
            "hello.txt,f572d396fae9206628714fb2ce00f72e94f2258f\n" +
            "```\n" +
            "\n";

    @Test
    @Ignore
    public void decentDemoOfAllTheConcepts() throws Exception {

        new File("merkleizer.db").delete();
        GetCentricMerkleizedSubversionDirectoriesExtendedForTesting svnMerkelizer = new GetCentricMerkleizedSubversionDirectoriesExtendedForTesting(
                "http://localhost:8098/svn/dataset/", "abc123", new SvnMerkleizer.Metrics.Console(), new HashMap<Integer, Integer>(), 8080);

        svnMerkelizer.start("server.join=false");
        while (!svnMerkelizer.appStarted()) {
            Thread.sleep(15);
        }

        // Vanilla Subversion directory does not have SvnMerkleizer adding information

        given().
                port(8098).
                auth().preemptive().basic("harry", "harrypw").
        when().
                get("/svn/dataset/A/AK/.merkle.csv").
        then().
                statusCode(404);


        // SvnMerkleizer decorating the same  directory

        given().
                port(8080).
                auth().preemptive().basic("harry", "harrypw").
        when().
                get("/abc123/A/AK/.merkle.csv").
        then().
                statusCode(200).
                body(equalToIgnoringWhiteSpace(
                        ",d19e7b3cade2b87a5031c71855191637ea8835b1\n" +
                                "A/,646f8a2439291fccbab6d9419ae0aa1b57a0d67b\n" +
                                "hello.txt,f572d396fae9206628714fb2ce00f72e94f2258f")).
                contentType("text/csv");


        assertEquals("", svnMerkelizer.journal);
        assertEquals(2, svnMerkelizer.cacheMiss);

        // SvnMerkleizer decorating the same directory via Servirtium which is recording the conversation

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InteractionManipulations manipulations = new SubversionInteractionManipulations("localhost:8100", "localhost:8080");
        InteractionMonitor interactionMonitor = new MarkdownRecorder(new ServiceInteropViaOkHttp(), manipulations) {
            @Override
            public void setOutputStream(String filename, OutputStream out) {
                super.setOutputStream(filename, byteArrayOutputStream);
            }
        };
        JettyServirtiumServer servirtiumServer = new JettyServirtiumServer(new ServiceMonitor.Default(), 8100, manipulations, interactionMonitor);
        servirtiumServer.start();

        interactionMonitor.setScriptFilename("src/test/mocks/csvTest2222.md");

        given().
                port(8100).
                auth().preemptive().basic("harry", "harrypw").
        when().
                get("/abc123/A/AK/.merkle.csv").
        then().
                statusCode(200).
                body(equalToIgnoringWhiteSpace(
                        ",d19e7b3cade2b87a5031c71855191637ea8835b1\n" +
                                "A/,646f8a2439291fccbab6d9419ae0aa1b57a0d67b\n" +
                                "hello.txt,f572d396fae9206628714fb2ce00f72e94f2258f")).
                contentType("text/csv");


        servirtiumServer.finishedScript();
        servirtiumServer.stop();

        String foo = byteArrayOutputStream.toString();
        assertEquals(servirtiumConversation, foo);

        svnMerkelizer.stop();

        // Servirtium replaying the recording of the GET SvnMerkleizer (which is no longer running itself)

        assertEquals("cache-hit=harry:/svn/dataset/A/AK/A/\n", svnMerkelizer.journal);
        assertEquals(2, svnMerkelizer.cacheMiss);

        MarkdownReplayer markdownReplayer = new MarkdownReplayer(new MarkdownReplayer.ReplayMonitor.Default()) {
            @Override
            public void setPlaybackConversation(String conversation) {
                super.setPlaybackConversation(servirtiumConversation);
            }
        };
        servirtiumServer = new JettyServirtiumServer(new ServiceMonitor.Default(), 8100, manipulations, markdownReplayer);
        servirtiumServer.start();

        markdownReplayer.setPlaybackConversation(servirtiumConversation);

        given().
                port(8100).
                auth().preemptive().basic("harry", "harrypw").
        when().
                get("/abc123/A/AK/.merkle.csv").
        then().
                statusCode(200).
                body(equalToIgnoringWhiteSpace(
                        ",d19e7b3cade2b87a5031c71855191637ea8835b1\n" +
                                "A/,646f8a2439291fccbab6d9419ae0aa1b57a0d67b\n" +
                                "hello.txt,f572d396fae9206628714fb2ce00f72e94f2258f")).
                contentType("text/csv");

        servirtiumServer.finishedScript();
        servirtiumServer.stop();



    }
}
