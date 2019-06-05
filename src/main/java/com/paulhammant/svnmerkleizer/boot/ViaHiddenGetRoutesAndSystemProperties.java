package com.paulhammant.svnmerkleizer.boot;

import com.paulhammant.svnmerkleizer.SubversionDirectoryMerkleizerService;
import com.paulhammant.svnmerkleizer.SvnMerkleizer;

public class ViaHiddenGetRoutesAndSystemProperties {

    public static void main(String[] args) {
        final SvnMerkleizer.Metrics.Console metrics1 = new SvnMerkleizer.Metrics.Console();
        final String delegateTo = System.getProperty("SvnMerkleizerDelegateTo");
        final String contextDir = System.getProperty("SvnMerkleizerContextDir");
        final String cacheFilePath = System.getProperty("SvnMerkleizerCacheFilePath");
        final String port = System.getProperty("SvnMerkleizerPort");
        new SubversionDirectoryMerkleizerService.ViaHiddenGetRoutes(delegateTo, contextDir, metrics1,
                new SvnMerkleizer(delegateTo, contextDir, metrics1, cacheFilePath),
                Integer.parseInt(port)
        ).start(args);
    }

}
