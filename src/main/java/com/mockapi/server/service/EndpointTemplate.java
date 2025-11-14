package com.mockapi.server.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Template for predefined endpoint configurations
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EndpointTemplate {
    private String name;
    private String path;
    private String method;
    private int statusCode;
    private String response;
    private String contentType;
}

