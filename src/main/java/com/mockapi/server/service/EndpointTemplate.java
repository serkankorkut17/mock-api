package com.mockapi.server.service;

import java.util.List;

/**
 * Template for predefined endpoint configurations
 */
public class EndpointTemplate {
    private String path;
    private String method;
    private int statusCode;
    private String response;
    private String contentType;

    public EndpointTemplate() {
    }

    public EndpointTemplate(String path, String method, int statusCode, String response, String contentType) {
        this.path = path;
        this.method = method;
        this.statusCode = statusCode;
        this.response = response;
        this.contentType = contentType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}

