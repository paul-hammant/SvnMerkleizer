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

import com.paulhammant.servirtium.InteractionMonitor;
import com.paulhammant.servirtium.ServirtiumServer;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;

public class TestMethods {

    public static void itHasABunchOfFunctionsUsingPreemptiveAuthenticationHandling(InteractionMonitor interactionMonitor, int port) throws IOException {

        {

            interactionMonitor.noteForNextInteraction("Test Context", ".merkle.csv retrieves CSV");

            given().
                    port(port).
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

        }

        {
            interactionMonitor.noteForNextInteraction("Test Context", ".merkle.txt retrieves TXT");

            given().
                    port(port).
                    auth().preemptive().basic("harry", "harrypw").
            when().
                    get("/abc123/A/AK/.merkle.txt").
            then().
                    statusCode(200).
                    body(equalToIgnoringWhiteSpace(
                            "d19e7b3cade2b87a5031c71855191637ea8835b1\n" +
                                    "A/ 646f8a2439291fccbab6d9419ae0aa1b57a0d67b\n" +
                                    "hello.txt f572d396fae9206628714fb2ce00f72e94f2258f")).
                    contentType("text/plain");

        }

        {
            interactionMonitor.noteForNextInteraction("Test Context", ".merkle.csv does not work if preemptive bad password");

            given().
                    port(port).
                    auth().preemptive().basic("harrypw", "sdfsdfewerwerwerwe").
            when().
                    get("/abc123/A/AK/.merkle.csv").
            then().
                    statusCode(401).
                    body(equalToIgnoringWhiteSpace(""));

        }

        {
            interactionMonitor.noteForNextInteraction("Test Context", ".merkle.xml retrieves XML");

            given().
                    port(port).
                    auth().preemptive().basic("harry", "harrypw").
            when().
                    get("/abc123/A/AK/.merkle.xml").
            then().
                    statusCode(200).
                    body(equalToIgnoringWhiteSpace(
                            "<directory>\n" +
                                    "  <sha1>d19e7b3cade2b87a5031c71855191637ea8835b1</sha1>\n" +
                                    "  <entry>\n" +
                                    "    <dir>A</dir>\n" +
                                    "    <sha1>646f8a2439291fccbab6d9419ae0aa1b57a0d67b</sha1>\n" +
                                    "  </entry>\n" +
                                    "  <entry>\n" +
                                    "    <file>hello.txt</file>\n" +
                                    "    <sha1>f572d396fae9206628714fb2ce00f72e94f2258f</sha1>\n" +
                                    "  </entry>\n" +
                                    "</directory>")).
                    contentType("text/xml");

        }

        {
            interactionMonitor.noteForNextInteraction("Test Context", ".merkle.html retrieves HTML");

            given().
                    port(port).
                    auth().preemptive().basic("harry", "harrypw").
            when().
                    get("/abc123/A/AK/.merkle.html").
            then().
                    statusCode(200).
                    body(equalToIgnoringWhiteSpace(
                            "<html><body>\n" +
                                    "<p>d19e7b3cade2b87a5031c71855191637ea8835b1</p>\n" +
                                    "<table>\n" +
                                    "<tr><td><a href=\"A/.merkle.html\">A</a></td><td>646f8a2439291fccbab6d9419ae0aa1b57a0d67b</td></tr>\n" +
                                    "<tr><td>hello.txt</td><td>f572d396fae9206628714fb2ce00f72e94f2258f</td></tr>\n" +
                                    "</table></body></html>")).
                    contentType("text/html");

        }

        {
            interactionMonitor.noteForNextInteraction("Test Context", "bogus URL results in 404 response");
            bogusURL404s("/abc123/blah/blah/.merkle.csv", port);

        }

        //
        {
            interactionMonitor.noteForNextInteraction("Test Context", "another bogus URL results in 404 response");
            bogusURL404s( "/blah/blah/.merkle.csv", port);

        }

        //
        {
            interactionMonitor.noteForNextInteraction("Test Context", ".merkle.json retrieves JSON");

            given().
                    port(port).
                    auth().preemptive().basic("harry", "harrypw").
            when().
                    get("/abc123/A/AK/.merkle.json").
            then().
                    statusCode(200).
                    body(equalToIgnoringWhiteSpace(
                            ("{\n" +
                                    "  \"sha1\" : \"d19e7b3cade2b87a5031c71855191637ea8835b1\",\n" +
                                    "  \"contents\" : [ {\n" +
                                    "    \"dir\" : \"A\",\n" +
                                    "    \"sha1\" : \"646f8a2439291fccbab6d9419ae0aa1b57a0d67b\"\n" +
                                    "  }, {\n" +
                                    "    \"file\" : \"hello.txt\",\n" +
                                    "    \"sha1\" : \"f572d396fae9206628714fb2ce00f72e94f2258f\"\n" +
                                    "  } ]\n" +
                                    "}").replace("'", "\""))).
                    contentType("application/json");
        }


    }
    public static void itHasABunchOfFunctionsWithoutPreemptiveAuthenticationHandling(InteractionMonitor interactionMonitor, int port) throws IOException {

        {
            interactionMonitor.noteForNextInteraction("Test Context", ".merkle.csv does not work with no attempt to authenticate");

            given().
                    port(port).
            when().
                    get("/abc123/A/AK/.merkle.csv").
            then().
                    statusCode(401).
                    body(equalToIgnoringWhiteSpace(""));

        }

        {

            interactionMonitor.noteForNextInteraction("Test Context", ".merkle.csv works after idiomatic authentication challenge");

            given().
                    port(port).
                    auth().basic("harry", "harrypw").
            when().
                    get("/abc123/A/AK/.merkle.csv").
            then().
                    statusCode(200).
                    body(equalToIgnoringWhiteSpace(
                            ",d19e7b3cade2b87a5031c71855191637ea8835b1\n" +
                                    "A/,646f8a2439291fccbab6d9419ae0aa1b57a0d67b\n" +
                                    "hello.txt,f572d396fae9206628714fb2ce00f72e94f2258f")).
                    contentType("text/csv");
        }

    }

    public static void bogusURL404s(String s, int port) throws IOException {

        given().
                port(port).
                auth().preemptive().basic("harry", "harrypw").
        when().
                get(s).
        then().
                statusCode(404).
                body(equalTo(""));
    }

    public static void testName(String name, ServirtiumServer servirtiumServer, InteractionMonitor interactionMonitor) throws IOException {

        String filename = "src/test/mocks/" + name + ".md";
        interactionMonitor.setScriptFilename(filename);
        servirtiumServer.setContext(name);
    }


    public static void getAndCheckHarrysRootCsv(int port) throws IOException {

        given().
                port(port).
                auth().preemptive().basic("harry", "harrypw").
        when().
                get("/abc123/.merkle.csv").
        then().
                statusCode(200).
                body(equalToIgnoringWhiteSpace(
                    ",d3744536d367068d2783fd342b978204b6245806\n" +
                    "A/,8c660e92cac202aa05413226f366dbfc8c5cf575\n" +
                    "C/,615d3025d655189dc171e923678ad567c6619d4c\n" +
                    "D/,1e4f641fac18a265540fd85ac801feb9f5000ccd\n" +
                    "F/,59cff862d4ff9f2b06f382718b14b3f51ebfa703\n" +
                    "G/,3a87c4328bc7fb090e8d8c0d89cab608390bcb29\n" +
                    "H/,4083a4c935d352500eee395edb1c49d28ac9c87d\n" +
                    "I/,1a5cfb8281e5bb92fbc37103a56b07b9ecbf709b\n" +
                    "K/,06d83643cf80b28b72027b029d2024360cf9d71f\n" +
                    "L/,2ae6132a7652ccb5e2341de43f4e3c99bf8caac5\n" +
                    "M/,830e5b9f87326a723477572f176698618fda663f\n" +
                    "N/,7f476bd69736136cefb91ffd9554d3afc1fd8a06\n" +
                    "O/,5f6690f8d73503cbb61345c9aede67513f526b6d\n" +
                    "P/,b94cf03d11eaae980e74f2ab0ea63948fc54f1cc\n" +
                    "R/,c538a5b0f9e2b0f5cc95e3cff8c3cd347583cce3\n" +
                    "S/,21707b4fef5a4acf921c141b012ad05c22ce3363\n" +
                    "T/,96422c4579df55a2f7cd5e58eed9397e34fae355\n" +
                    "U/,7f81bad8d060541ff49e7cbd293dc4cb43808715\n" +
                    "V/,d1814e93c433d307595e1039d622a1d4089fe4a2\n" +
                    "W/,430412a9806ac466e3543aa80fd3c3a59afea686")).
                contentType("text/csv");
    }

    public static void getAndCheckSallysRootTxt(int port) throws IOException {

        given().
                port(port).
                auth().preemptive().basic("sally", "sallypw").
        when().
                get("/abc123/.merkle.txt").
        then().
                statusCode(200).
                body(equalToIgnoringWhiteSpace("a276e1bb124d965cf33960e80f2696be328cd4dc\n" +
                    "A/ 8c660e92cac202aa05413226f366dbfc8c5cf575\n" +
                    "F/ 8319208bd14dd79e96e0facf61a9945c09aa3444\n" +
                    "W/ 430412a9806ac466e3543aa80fd3c3a59afea686")).
                contentType("text/plain");

        // In the above A/ and W/ are the same as Harry's, but F/ is different.
        // F/ Being different and many others at root level missing means the root SHA1
        // is different too.
    }


}
