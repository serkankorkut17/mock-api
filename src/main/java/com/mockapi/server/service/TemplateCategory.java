package com.mockapi.server.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Category containing multiple endpoint templates
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TemplateCategory {
    private String name;
    private List<EndpointTemplate> endpoints;

}

