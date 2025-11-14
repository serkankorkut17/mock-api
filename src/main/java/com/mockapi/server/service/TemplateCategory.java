package com.mockapi.server.service;

import java.util.List;

/**
 * Category containing multiple endpoint templates
 */
public class TemplateCategory {
    private String name;
    private String icon;
    private List<EndpointTemplate> endpoints;

    public TemplateCategory() {
    }

    public TemplateCategory(String name, String icon, List<EndpointTemplate> endpoints) {
        this.name = name;
        this.icon = icon;
        this.endpoints = endpoints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<EndpointTemplate> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<EndpointTemplate> endpoints) {
        this.endpoints = endpoints;
    }
}

