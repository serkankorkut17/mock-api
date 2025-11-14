package com.mockapi.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing endpoint templates
 */
@Service
public class TemplateService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<TemplateCategory> categories;

    public TemplateService() {
        loadTemplates();
    }

    /**
     * Load templates from JSON file
     */
    private void loadTemplates() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates.json");
            if (inputStream != null) {
                categories = objectMapper.readValue(inputStream, new TypeReference<List<TemplateCategory>>() {});
                System.out.println("✅ Loaded " + categories.size() + " template categories");
            } else {
                System.out.println("⚠️ templates.json not found, using empty list");
                categories = new ArrayList<>();
            }
        } catch (IOException e) {
            System.out.println("❌ Error loading templates: " + e.getMessage());
            categories = new ArrayList<>();
        }
    }

    /**
     * Get all template categories
     */
    public List<TemplateCategory> getCategories() {
        return categories;
    }

    /**
     * Get templates by category name
     */
    public List<EndpointTemplate> getTemplatesByCategory(String categoryName) {
        return categories.stream()
                .filter(cat -> cat.getName().equals(categoryName))
                .findFirst()
                .map(TemplateCategory::getEndpoints)
                .orElse(new ArrayList<>());
    }

    /**
     * Reload templates from file
     */
    public void reloadTemplates() {
        loadTemplates();
    }
}

