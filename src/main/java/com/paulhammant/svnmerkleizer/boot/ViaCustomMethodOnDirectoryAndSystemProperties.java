package com.paulhammant.svnmerkleizer.boot;

import com.paulhammant.svnmerkleizer.SvnMerkleizer;
import okhttp3.OkHttpClient;

public class ViaCustomMethodOnDirectoryAndSystemProperties {

    public static void main(String[] args) {
        final String delegateTo = System.getProperty("SvnMerkleizerDelegateTo");
        final String contextDir = System.getProperty("SvnMerkleizerContextDir");
        final String cacheFilePath = System.getProperty("SvnMerkleizerCacheFilePath");
        final String port = System.getProperty("SvnMerkleizerPort");
        final String method = System.getProperty("SvnMerkleizerMethod");
        new ViaCustomMethodOnDirectory(method,
                delegateTo, contextDir, cacheFilePath,
                new SvnMerkleizer.Metrics.NullObject(), Integer.parseInt(port),
                new OkHttpClient()
        ).start(args);
    }
}
