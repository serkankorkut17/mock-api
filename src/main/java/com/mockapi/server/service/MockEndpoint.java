package com.mockapi.server.service;

import lombok.Getter;

@Getter
public class MockEndpoint {
    private final String path;
    private final String method;
    private final int statusCode;
    private final String response;
    private final String contentType;

    public MockEndpoint(String path, String method, int statusCode, String response) {
        this(path, method, statusCode, response, "application/json");
    }

    public MockEndpoint(String path, String method, int statusCode, String response, String contentType) {
        this.path = path;
        this.method = method;
        this.statusCode = statusCode;
        this.response = response;
        this.contentType = contentType != null ? contentType : "application/json";
    }

}

