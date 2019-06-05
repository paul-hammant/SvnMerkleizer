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
import org.junit.*;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;

public class CustomHttpMethodDirectServiceTests {

    private TestExtendedSubversionDirectoryMerkleizerService.TestingSubversionDirectoryMerkleizerService merkleizerService;
    private ServirtiumServer servirtiumServer = new ServirtiumServer.NullObject();
    private InteractionMonitor interactionMonitor = new InteractionMonitor.NullObject();

    @Before
    public void setup() {
        new File("merkleizer.db").delete();
        merkleizerService = new TestExtendedSubversionDirectoryMerkleizerService.SubversionDirectoryMerkleizerServiceViaCustomMethodOnDirectory();
        startTestApp();
    }

    @After
    public void tearDown() {
        stopTestApp();
    }

    private void startTestApp() {
        long start = System.currentTimeMillis();
        merkleizerService.start("server.join=false");
        while (!merkleizerService.appStarted()) {
            sleep(start);
        }
    }

    private void stopTestApp() {
        long start = System.currentTimeMillis();
        merkleizerService.stop();
        while (!merkleizerService.appStopped()) {
            sleep(start);
        }
        merkleizerService = null;
    }

    static void sleep(long start) {
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
        }
        long dur = System.currentTimeMillis() - start;
        if (dur > 5000) {
            fail("Application didn't start for some reason - port in use?");
        }
    }

    @Test
    public void aBunchOfFunctionalTests() throws IOException {

        // request 'expects-type' header in the HTTP request is

        itShouldInvokeWorkflowForExpectsType("html()", "HTML", 8080);
        itShouldInvokeWorkflowForExpectsType("csv()", "CSV", 8080);
        itShouldInvokeWorkflowForExpectsType("json()", "JSON", 8080);
        itShouldInvokeWorkflowForExpectsType("xml()", "XML", 8080);
        itShouldInvokeWorkflowForExpectsType("txt()", "TXT", 8080);

        // XXX (not a legitimate choice) - which results a 500 response
        given().
                port(8080).
                header("expects-type", "XXX").
        when().
                request("TESTING-CUSTOM-METHOD", "/merkle/A/AK/").
        then().
                statusCode(500).
                body(equalToIgnoringWhiteSpace("Specified 'expects-type' header of 'XXX' isn't supported. JSON, CSV, TXT, XML, HTML are the allowed ones"));

        // completely missing - which results a 500 response
        given().
                port(8080).
        when().
                request("TESTING-CUSTOM-METHOD", "/merkle/A/AK/").
        then().
                statusCode(500).
                body(equalToIgnoringWhiteSpace("The 'expects-type' header should have been set"));

        // requests to non-directories are not allowed
        given().
                port(8080).
                header("expects-type", "JSON").
        when().
                request("TESTING-CUSTOM-METHOD", "/merkle/A/AK/eeee").
        then().
                statusCode(500).
                body(equalToIgnoringWhiteSpace("You can only do TESTING-CUSTOM-METHOD operations on directories"));

        // bogus URL 1 is not there.
        given().
                port(8080).
        when().
                get("/abc123/blah/blah/.merkle.csv").
        then().
                statusCode(404).
                body(equalTo(""));

        // bogus URL 2 is not there.
        given().
                port(8080).
        when().
                get("/abc123/blah/.merkle.csv").
        then().
                statusCode(404).
                body(equalTo(""));

    }

    private void itShouldInvokeWorkflowForExpectsType(String methodName, String expectsType, int port) {
        given().
                port(port).
                header("expects-type", expectsType).
                when().
                request("TESTING-CUSTOM-METHOD", "/merkle/A/AK/").
                then().
                statusCode(200).
                body(equalTo(methodName));
        // ^ from over ridden methods of CustomMethodMerkleizedSubversionDirectoriesExtendedForTesting
    }

}
