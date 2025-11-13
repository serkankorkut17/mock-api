package com.mockapi.server.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Service
public class MockService {

    private final Map<String, MockEndpoint> mockEndpoints = new ConcurrentHashMap<>();

    public void addMock(String path, String method, String response) {
        addMock(path, method, 200, response);
    }

    public void addMock(String path, String method, int statusCode, String response) {
        mockEndpoints.put(path + "_" + method, new MockEndpoint(path, method, statusCode, response));
    }

    public String getMockResponse(String path, String method) {
        MockEndpoint mock = mockEndpoints.get(path + "_" + method);
        return mock != null ? mock.getResponse() : null;
    }

    public MockEndpoint getMockEndpoint(String path, String method) {
        return mockEndpoints.get(path + "_" + method);
    }

    public List<MockEndpoint> getMocksList() {
        return new ArrayList<>(mockEndpoints.values());
    }

    public boolean removeMock(String path, String method) {
        String key = path + "_" + method;
        MockEndpoint removed = mockEndpoints.remove(key);
        return removed != null;
    }

    public void clearAllMocks() {
        mockEndpoints.clear();
    }
}

