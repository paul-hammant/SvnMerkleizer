package com.paulhammant.svnmerkleizer.pojos;

import okhttp3.Response;

import java.io.IOException;

public class SvnResponse {

    private final Response propfindResponse;

    public SvnResponse(Response propfindResponse) {

        this.propfindResponse = propfindResponse;
    }

    public int statusCode() {
        return propfindResponse.code();
    }

    public void close() {
        propfindResponse.close();
    }

    public String body() throws IOException {
        return propfindResponse.body().string();
    }
}
