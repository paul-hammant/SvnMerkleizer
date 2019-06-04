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

import com.paulhammant.svnmerkleizer.pojos.Directory;
import com.paulhammant.svnmerkleizer.pojos.VersionInfo;
import org.jooby.RequestLogger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class GetCentricMerkleizedSubversionDirectoriesExtendedForTesting
        extends SubversionDirectoryMerkleizerService.ViaHiddenGetRoutes
        implements TestingSubversionDirectoryMerkleizerService {

    public boolean appStarted;
    private boolean appStopped;
    int cacheMiss = 0;
    private Map<Integer, Integer> revCounts;

    String journal = "";
    private String requestLog = "";

    private SvnMerkleizer svnMerkleizerForTesting;

    public GetCentricMerkleizedSubversionDirectoriesExtendedForTesting(String delegateToUrl, String contextDir, SvnMerkleizer.Metrics metrics, HashMap<Integer, Integer> revCounts, int port) {
        super(delegateToUrl, contextDir, metrics, null, port);

        // hack for testing
        try {
            Field smf = SubversionDirectoryMerkleizerService.class.getDeclaredField("svnMerkleizer");
            smf.setAccessible(true);
            svnMerkleizerForTesting = new SvnMerkleizer(delegateToUrl, contextDir, metrics, "merkleizer.db") {
                @Override
                public void cacheMiss(String cacheKey) {
                    cacheMiss++;
                }

                @Override
                public void cacheHit(String cacheKey) {
                    journal += "cache-hit=" + cacheKey + "\n";
                }

                @Override
                void makeVersionInfoForPath(int xmlHashCode, Directory directory, int rvn, String pathPart) {
                    super.makeVersionInfoForPath(xmlHashCode, directory, rvn, pathPart);
                    revCounts.merge(rvn, 1, (a, b) -> a + b);
                }
            };
            smf.set(this, svnMerkleizerForTesting);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new UnsupportedOperationException("not expecting reflection to fail here");
        }

        this.revCounts = revCounts;
        use("*", new RequestLogger().log(line -> {
            requestLog = requestLog + "\n" + line;
        }));
        onStarted(() -> {
            appStarted = true;
        });
        onStop(() -> {
            appStopped = true;
        });
    }

    public void wipeJournal() {
        journal = "";
        requestLog = "";
        cacheMiss = 0;
        revCounts.clear();
    }

    public void split() {
        writeSummaryToJournal();
        journal += "--split--\n";
        cacheMiss = 0;
        revCounts.clear();
    }

    public void writeSummaryToJournal() {
        journal += "cache-misses=" + cacheMiss + "\n";
        journal += "revs=" + revCounts + "\n";
    }

    @Override
    public Object getJournal() {
        return journal;
    }

    @Override
    public boolean appStarted() {
        return appStarted;
    }

    @Override
    public boolean appStopped() {
        return appStopped;
    }

    @Override
    public String getRequestLog() {
        return requestLog;
    }

    public void setCacheItemsToRev1AsIfThatWereRealityInTheSvnRepo() {
        try {
            Map<String, VersionInfo> entries = new HashMap<>();
            Field c = SvnMerkleizer.class.getDeclaredField("cache");
            c.setAccessible(true);
            Map<String, VersionInfo> cache = (Map<String, VersionInfo>) c.get(svnMerkleizerForTesting);
            cache.forEach((k, v) -> {
                v.svnRevision = 1;
                entries.put(k, v);
            });
            entries.forEach((k, v) -> {
                cache.put(k, v);
            });
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new UnsupportedOperationException("really not expecting reflection to fail here");
        }

    }

}
