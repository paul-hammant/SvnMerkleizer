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

package com.paulhammant.svnmerkleizer.hiddengetroutes;

import com.paulhammant.servirtium.InteractionMonitor;
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
import static com.paulhammant.svnmerkleizer.TestMethods.getAndCheckSallysRootTxt;
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
        |     Port 8080     |
        | (plus disk cache) |
        +-------------------+
                    |
                    | Multiple HTTP "PROPFIND" and "OPTIONS" calls (OkHttp)
                    |
                    V
        +----------------------+
        |      Subversion      |
        |      + Apache        |    <- This is a Docker container
        |    + MOD_DAV_SVN     |       "test harness". It is
        |   + test commits     |       separately cloned *
        |     in Docker        |
        | Port 8098 externally |
        | (Port 80 internally) |
        +----------------------+

        * https://github.com/paul-hammant-fork/svnmerkleizer-test-repo

     */


public class DirectServiceTests {

    private static final int PORT = 8080;
    private InteractionMonitor interactionMonitor = new InteractionMonitor.NullObject();
    private TestExtendedSubversionDirectoryMerkleizerService.TestingSubversionDirectoryMerkleizerService merkleizerService;

    private List<Long> durationJournal;
    private List<SvnMerkleizer.Counts> countsJournal;

    @Before
    public void setup() {

        durationJournal = new ArrayList<>();
        countsJournal = new ArrayList<>();
        SvnMerkleizer.Metrics metrics = (durationMillis, counts) -> {
            durationJournal.add(durationMillis);
            countsJournal.add(counts);
        };
        new File("merkleizer.db").delete();
        merkleizerService = new TestExtendedSubversionDirectoryMerkleizerService.ViaHiddenGetRoutes(
                "http://localhost:8098/svn/dataset/", "abc123", metrics,
                new HashMap<>(), PORT);
        long start = System.currentTimeMillis();
        merkleizerService.start("server.join=false");
        while (!merkleizerService.appStarted()) {
            sleep(start);
        }

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

    @After
    public void tearDown() {
        long start = System.currentTimeMillis();
        merkleizerService.stop();
        while (!merkleizerService.appStopped()) {
            sleep(start);
        }
        merkleizerService = null;
    }

    @Test
    public void itHasABunchOfFunctionsUsingPreemptiveAuthenticationHandling() throws IOException {

        TestMethods.itHasABunchOfFunctionsUsingPreemptiveAuthenticationHandling(interactionMonitor, PORT);
    }

    @Test
    public void itHasABunchOfFunctionsWithoutPreemptiveAuthenticationHandling() throws IOException {

        TestMethods.itHasABunchOfFunctionsWithoutPreemptiveAuthenticationHandling(interactionMonitor, PORT);
    }

    @Test
    public void theMerkleTreeShouldBeAbleToTrackChangesToSubversionBetweenRequests() throws IOException {

        getAndCheckHarrysRootCsv(PORT); // fills cache (if it was not filled already)
        merkleizerService.setCacheItemsToRev1AsIfThatWereRealityInTheSvnRepo(); // pretends they're all rev #1 in the cache
        merkleizerService.wipeJournal(); // updates cache, effectively moving part of the tree to rev #2
        getAndCheckHarrysRootCsv(PORT);
        merkleizerService.writeSummaryToJournal();
        Assertions.assertThat(merkleizerService.getJournal())
                .isEqualTo("cache-hit=harry:/svn/dataset/A/AR/\n" +
                        "cache-hit=harry:/svn/dataset/A/AZ/\n" +
                        "cache-hit=harry:/svn/dataset/A/AK/A/\n" +
                        "cache-hit=harry:/svn/dataset/A/AL/\n" +
                        "cache-hit=harry:/svn/dataset/C/\n" +
                        "cache-hit=harry:/svn/dataset/D/\n" +
                        "cache-hit=harry:/svn/dataset/F/\n" +
                        "cache-hit=harry:/svn/dataset/G/\n" +
                        "cache-hit=harry:/svn/dataset/H/\n" +
                        "cache-hit=harry:/svn/dataset/I/\n" +
                        "cache-hit=harry:/svn/dataset/K/\n" +
                        "cache-hit=harry:/svn/dataset/L/\n" +
                        "cache-hit=harry:/svn/dataset/M/\n" +
                        "cache-hit=harry:/svn/dataset/N/\n" +
                        "cache-hit=harry:/svn/dataset/O/\n" +
                        "cache-hit=harry:/svn/dataset/P/\n" +
                        "cache-hit=harry:/svn/dataset/R/\n" +
                        "cache-hit=harry:/svn/dataset/S/\n" +
                        "cache-hit=harry:/svn/dataset/T/\n" +
                        "cache-hit=harry:/svn/dataset/U/\n" +
                        "cache-hit=harry:/svn/dataset/V/\n" +
                        "cache-hit=harry:/svn/dataset/W/\n" +
                        "cache-misses=2\n" +
                        "revs={2=3}\n");
        Assertions.assertThat(merkleizerService.getRequestLog()).isEqualTo("");

    }

    @Test
    public void theMerkleTreeShouldBeAbleToTrackChangesToSubversionBetweenRequestsForAlternateAuthz() throws IOException {

        merkleizerService.deleteCacheKeys();

        countsJournal.clear();
        durationJournal.clear();

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
                .isEqualTo("cache-hit=sally:/svn/dataset/A/AR/\n" +
                        "cache-hit=sally:/svn/dataset/A/AZ/\n" +
                        "cache-hit=sally:/svn/dataset/A/AK/A/\n" +
                        "cache-hit=sally:/svn/dataset/A/AL/\n" +
                        "cache-hit=sally:/svn/dataset/F/\n" +
                        "cache-hit=sally:/svn/dataset/W/\n" +
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
