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

package com.paulhammant.svnmerkleizer.hiddengetroutes.recorded.svnmerkleizer;

import com.paulhammant.servirtium.*;
import com.paulhammant.servirtium.jetty.JettyServirtiumServer;
import com.paulhammant.servirtium.svn.SubversionInteractionManipulations;
import com.paulhammant.svnmerkleizer.SvnMerkleizer;
import com.paulhammant.svnmerkleizer.TestExtendedSubversionDirectoryMerkleizerService;
import com.paulhammant.svnmerkleizer.TestMethods;
import org.assertj.core.api.Assertions;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.paulhammant.svnmerkleizer.TestMethods.*;
import static com.paulhammant.svnmerkleizer.TestMethods.getAndCheckSallysRootTxt;
import static org.assertj.core.api.Assertions.fail;
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
                 +------------+
                 | Servirtium |   <- In playback mode.
                 |  Port 8100 |      Of recordings in src/test/mocks/
                 +------------+


     */


public class PlayingBackSvnMerkleizerServiceTests {

    private static final int PORT = 8100;
    private ServirtiumServer servirtiumServer;
    private InteractionMonitor interactionMonitor;

    private TestExtendedSubversionDirectoryMerkleizerService.TestingSubversionDirectoryMerkleizerService merkleizerService;

    private List<Long> durationJournal = new ArrayList<>();
    private List<SvnMerkleizer.Counts> countsJournal = new ArrayList<>();

    private SvnMerkleizer.Metrics metrics = (durationMillis, counts) -> {
        durationJournal.add(durationMillis);
        countsJournal.add(counts);
    };


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

    @Before
    public void setup() throws Exception {
        InteractionManipulations manipulations = new SubversionInteractionManipulations("localhost:8100", "localhost:8080");
        interactionMonitor =new MarkdownReplayer(new MarkdownReplayer.ReplayMonitor.Default())
                .withAlphaSortingOfHeaders();
        servirtiumServer = new JettyServirtiumServer(new ServiceMonitor.Default(), PORT, manipulations, interactionMonitor);
        servirtiumServer.start();

        new File("merkleizer.db").delete();
        merkleizerService = new TestExtendedSubversionDirectoryMerkleizerService.NullObject();
    }

    @After
    public void tearDown() {
        servirtiumServer.finishedScript();
        servirtiumServer.stop();
        servirtiumServer = null;
    }

    @Test
    public void itHasABunchOfFunctionsUsingPreemptiveAuthenticationHandling() throws IOException {

        testName("svnmerkleizer/itHasABunchOfFunctionsUsingPreemptiveAuthenticationHandling", servirtiumServer, interactionMonitor);

        TestMethods.itHasABunchOfFunctionsUsingPreemptiveAuthenticationHandling(interactionMonitor, PORT);
    }

    @Test
    public void itHasABunchOfFunctionsWithoutPreemptiveAuthenticationHandling() throws IOException {

        testName("svnmerkleizer/itHasABunchOfFunctionsWithoutPreemptiveAuthenticationHandling", servirtiumServer, interactionMonitor);

        TestMethods.itHasABunchOfFunctionsWithoutPreemptiveAuthenticationHandling(interactionMonitor, PORT);
    }

    @Test
    public void theMerkleTreeShouldBeAbleToTrackChangesToSubversionBetweenRequests() throws IOException {

        testName("svnmerkleizer/theMerkleTreeShouldBeAbleToTrackChangesToSubversionBetweenRequests", servirtiumServer, interactionMonitor);

        getAndCheckHarrysRootCsv(PORT); // fills cache (if it was not filled already)
        merkleizerService.setCacheItemsToRev1AsIfThatWereRealityInTheSvnRepo(); // pretends they're all rev #1 in the cache
        merkleizerService.wipeJournal(); // updates cache, effectively moving part of the tree to rev #2
        merkleizerService.writeSummaryToJournal();
        Assertions.assertThat(merkleizerService.getJournal()).isEqualTo("cache-misses=0\nrevs={}\n");
        Assertions.assertThat(merkleizerService.getRequestLog()).isEqualTo("");

        getAndCheckHarrysRootCsv(PORT);
        merkleizerService.wipeJournal(); // updates cache, effectively moving part of the tree to rev #2
        merkleizerService.writeSummaryToJournal();

        Assertions.assertThat(merkleizerService.getJournal()).isEqualTo("cache-misses=0\nrevs={}\n");
        Assertions.assertThat(merkleizerService.getRequestLog()).isEqualTo("");

    }

    @Test
    public void theMerkleTreeShouldBeAbleToTrackChangesToSubversionBetweenRequestsForAlternateAuthz() throws IOException {

        testName("svnmerkleizer/theMerkleTreeShouldBeAbleToTrackChangesToSubversionBetweenRequestsForAlternateAuthz", servirtiumServer, interactionMonitor);

        merkleizerService.deleteCacheKeys();

        countsJournal.clear();
        durationJournal.clear();

        getAndCheckSallysRootTxt(PORT); // fills cache (if it was not filled already)

        // No hits on SvnMerkleizer Service
        assertEquals(0, countsJournal.size());

        countsJournal.clear();
        durationJournal.clear();

        merkleizerService.setCacheItemsToRev1AsIfThatWereRealityInTheSvnRepo(); // pretends they're all rev #1 in the cache
        merkleizerService.wipeJournal(); // updates cache, effectively moving part of the tree to rev #2
        getAndCheckSallysRootTxt(PORT);

        merkleizerService.writeSummaryToJournal();
        // No hits on SvnMerkleizer Service
        Assertions.assertThat(merkleizerService.getJournal())
                .isEqualTo("cache-misses=0\n" +
                        "revs={}\n");
        Assertions.assertThat(merkleizerService.getRequestLog()).isEqualTo("");

        // No hits on SvnMerkleizer Service
        assertEquals(0, countsJournal.size());

    }

}
