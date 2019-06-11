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

package com.paulhammant.svnmerkleizer.hiddengetroutes.recorded.subversion;

import com.paulhammant.servirtium.*;
import com.paulhammant.servirtium.jetty.JettyServirtiumServer;
import com.paulhammant.servirtium.svn.SubversionInteractionManipulations;
import com.paulhammant.svnmerkleizer.SvnMerkleizer;
import com.paulhammant.svnmerkleizer.TestExtendedSubversionDirectoryMerkleizerService;
import com.paulhammant.svnmerkleizer.TestMethods;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.paulhammant.svnmerkleizer.TestMethods.*;
import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

    /*

          +-----------+
          | TEST      |
          |      CODE |
          +-----------+
                    |
                    | RestAssured doing HTTP "GET" calls to
                    | .merkle.csv | txt | html | xml resources
                    |
                    V
        +-------------------+
        |   SvnMerkleizer   |
        |     Port 9080     |
        | (plus disk cache) |
        +-------------------+
                    |
                    | Multiple HTTP "PROPFIND" and "OPTIONS" calls (OkHttp)
                    |
                    V
             +------------+
             | Servirtium |   <- In playback mode.
             |  Port 8198 |       Of recordings in src/test/mocks/
             +------------+

     */


public class PlayingBackSubversionServiceTests {

    static final int PORT = 9080;
    private ServirtiumServer servirtiumServer;
    private InteractionMonitor interactionMonitor;

    private TestExtendedSubversionDirectoryMerkleizerService.TestingSubversionDirectoryMerkleizerService merkleizerService;

    private List<Long> durationJournal;
    private List<SvnMerkleizer.Counts> countsJournal ;
    private SvnMerkleizer.Metrics metrics;

    private static void sleep(long start) {
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
        }
        long dur = System.currentTimeMillis() - start;
        if (dur > 5000) {
            fail("Application didn't start for some reason - port in use?");
        }
    }

    @Before
    public void setup() throws Exception {

        durationJournal = new ArrayList<>();
        countsJournal = new ArrayList<>();

        metrics = (durationMillis, counts) -> {
            durationJournal.add(durationMillis);
            countsJournal.add(counts);
        };

        InteractionManipulations manipulations = new SubversionInteractionManipulations(
                "localhost:8198/abc123", "localhost:8098/svn/dataset") {
            @Override
            public String changeSingleHeaderReturnedBackFromRealServiceForRecording(int ix, String headerBackFromService) {
                if (headerBackFromService.startsWith("Date: ")) {
                    return "Date: Wed, 01 Jan 2019 01:01:01 GMT";
                }
                return headerBackFromService;
            }
        };

        interactionMonitor =new MarkdownReplayer(
                new MarkdownReplayer.ReplayMonitor.Default());

        servirtiumServer = new JettyServirtiumServer(
                new ServiceMonitor.Default(), 8198,
                manipulations, interactionMonitor);

        servirtiumServer.start();

        new File("merkleizer.db").delete();
        merkleizerService = new TestExtendedSubversionDirectoryMerkleizerService.SubversionDirectoryMerkleizerServiceViaHiddenGetRoutes(
                "http://localhost:8198/abc123/", "abc123", metrics,
                new HashMap<>(), PORT);
        long start = System.currentTimeMillis();
        merkleizerService.start("server.join=false");
        while (!merkleizerService.appStarted()) {
            sleep(start);
        }
    }

    @After
    public void tearDown() {
        servirtiumServer.stop();
        long start = System.currentTimeMillis();
        merkleizerService.stop();
        while (!merkleizerService.appStopped()) {
            sleep(start);
        }
        merkleizerService = null;
    }

    @Test
    public void itHasABunchOfFunctionsUsingPreemptiveAuthenticationHandling() throws IOException {

        testName("subversion/itHasABunchOfFunctionsUsingPreemptiveAuthenticationHandling", servirtiumServer, interactionMonitor);

        TestMethods.itHasABunchOfFunctionsUsingPreemptiveAuthenticationHandling(interactionMonitor, PORT);
    }

    @Test
    public void itHasABunchOfFunctionsWithoutPreemptiveAuthenticationHandling() throws IOException {

        testName("subversion/itHasABunchOfFunctionsWithoutPreemptiveAuthenticationHandling", servirtiumServer, interactionMonitor);

        TestMethods.itHasABunchOfFunctionsWithoutPreemptiveAuthenticationHandling(interactionMonitor, PORT);
    }

    @Test
    public void theMerkleTreeShouldBeAbleToTrackChangesToSubversionBetweenRequests() throws IOException {

        testName("subversion/theMerkleTreeShouldBeAbleToTrackChangesToSubversionBetweenRequests", servirtiumServer, interactionMonitor);

        merkleizerService.deleteCacheKeys();

        countsJournal.clear();
        durationJournal.clear();

        interactionMonitor.noteForNextInteraction("Test Context", "Get a CSV for harry at root of repo then\n" +
                "Manipulate the tree and force cache-hits to show some efficiency");

        getAndCheckHarrysRootCsv(PORT); // fills cache (if it was not filled already)

        assertEquals(1, countsJournal.size());
        assertEquals(8002, countsJournal.get(0).propfind);
        assertEquals(4001, countsJournal.get(0).options);

        long uncachedDuration = durationJournal.get(0);

        countsJournal.clear();
        durationJournal.clear();

        merkleizerService.setCacheItemsToRev1AsIfThatWereRealityInTheSvnRepo(); // pretends they're all rev #1 in the cache
        merkleizerService.wipeJournal(); // updates cache, effectively moving part of the tree to rev #2
        getAndCheckHarrysRootCsv(PORT);

        merkleizerService.writeSummaryToJournal();
        Assertions.assertThat(merkleizerService.getJournal())
                .isEqualTo("cache-hit=harry:/abc123/A/AR/\n" +
                        "cache-hit=harry:/abc123/A/AZ/\n" +
                        "cache-hit=harry:/abc123/A/AK/A/\n" +
                        "cache-hit=harry:/abc123/A/AL/\n" +
                        "cache-hit=harry:/abc123/C/\n" +
                        "cache-hit=harry:/abc123/D/\n" +
                        "cache-hit=harry:/abc123/F/\n" +
                        "cache-hit=harry:/abc123/G/\n" +
                        "cache-hit=harry:/abc123/H/\n" +
                        "cache-hit=harry:/abc123/I/\n" +
                        "cache-hit=harry:/abc123/K/\n" +
                        "cache-hit=harry:/abc123/L/\n" +
                        "cache-hit=harry:/abc123/M/\n" +
                        "cache-hit=harry:/abc123/N/\n" +
                        "cache-hit=harry:/abc123/O/\n" +
                        "cache-hit=harry:/abc123/P/\n" +
                        "cache-hit=harry:/abc123/R/\n" +
                        "cache-hit=harry:/abc123/S/\n" +
                        "cache-hit=harry:/abc123/T/\n" +
                        "cache-hit=harry:/abc123/U/\n" +
                        "cache-hit=harry:/abc123/V/\n" +
                        "cache-hit=harry:/abc123/W/\n" +
                        "cache-misses=2\n" +
                        "revs={2=3}\n");
        Assertions.assertThat(merkleizerService.getRequestLog()).isEqualTo("");

        assertEquals(1, countsJournal.size());
        assertEquals(50, countsJournal.get(0).propfind);
        assertEquals(25, countsJournal.get(0).options);

        long cachedDuration = durationJournal.get(0);

        assertThat((int) (uncachedDuration/cachedDuration), Matchers.greaterThan(130));

    }

    @Test
    public void theMerkleTreeShouldBeAbleToTrackChangesToSubversionBetweenRequestsForAlternateAuthz() throws IOException {

        testName("subversion/theMerkleTreeShouldBeAbleToTrackChangesToSubversionBetweenRequestsForAlternateAuthz", servirtiumServer, interactionMonitor);

        merkleizerService.deleteCacheKeys();

        countsJournal.clear();
        durationJournal.clear();

        interactionMonitor.noteForNextInteraction("Test Context", "Get a CSV for sally at root of repo then\n" +
                "Manipulate the tree and force cache-hits to show some efficiency");

        getAndCheckSallysRootTxt(PORT); // fills cache (if it was not filled already)

        assertEquals(1, countsJournal.size());
        assertEquals(960, countsJournal.get(0).propfind);
        assertEquals(480, countsJournal.get(0).options);

        long uncachedDuration = durationJournal.get(0);

        countsJournal.clear();
        durationJournal.clear();

        merkleizerService.setCacheItemsToRev1AsIfThatWereRealityInTheSvnRepo(); // pretends they're all rev #1 in the cache
        merkleizerService.wipeJournal(); // updates cache, effectively moving part of the tree to rev #2
        getAndCheckSallysRootTxt(PORT);

        merkleizerService.writeSummaryToJournal();
        Assertions.assertThat(merkleizerService.getJournal())
                .isEqualTo("cache-hit=sally:/abc123/A/AR/\n" +
                        "cache-hit=sally:/abc123/A/AZ/\n" +
                        "cache-hit=sally:/abc123/A/AK/A/\n" +
                        "cache-hit=sally:/abc123/A/AL/\n" +
                        "cache-hit=sally:/abc123/F/\n" +
                        "cache-hit=sally:/abc123/W/\n" +
                        "cache-misses=2\n" +
                        "revs={2=3}\n");

        Assertions.assertThat(merkleizerService.getRequestLog()).isEqualTo("");

        assertEquals(1, countsJournal.size());
        assertEquals(18, countsJournal.get(0).propfind);
        assertEquals(9, countsJournal.get(0).options);

        long cachedDuration = durationJournal.get(0);

        assertThat((int) (uncachedDuration/cachedDuration), Matchers.greaterThan(38));

    }

}
