package com.mockapi.server.service;

public class MockEndpoint {
    private final String path;
    private final String method;
    private final int statusCode;
    private final String response;

    public MockEndpoint(String path, String method, int statusCode, String response) {
        this.path = path;
        this.method = method;
        this.statusCode = statusCode;
        this.response = response;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponse() {
        return response;
    }
}

