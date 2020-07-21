package com.paulhammant.svnmerkleizer.boot;

import com.paulhammant.svnmerkleizer.SubversionDirectoryMerkleizerService;
import com.paulhammant.svnmerkleizer.SvnMerkleizer;
import okhttp3.OkHttpClient;

public class ViaHiddenGetRoutesAndCommandLineArgs {

    public static void main(String[] args) {
        final SvnMerkleizer.Metrics.Console metrics1 = new SvnMerkleizer.Metrics.Console();
        final String delegateTo = args[0];
        final String contextDir = args[1];
        final String cacheFilePath = args[2];
        final String port = args[3];
        new SubversionDirectoryMerkleizerService.ViaHiddenGetRoutes(delegateTo, contextDir, metrics1,
                new SvnMerkleizer(delegateTo, contextDir, metrics1, cacheFilePath, new OkHttpClient()),
                Integer.parseInt(port), new OkHttpClient()
        ).start(args);
    }

}
