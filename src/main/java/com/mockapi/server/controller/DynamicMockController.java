package com.mockapi.server.controller;

import com.mockapi.server.service.MockEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import com.mockapi.server.service.MockService;

@RestController
public class DynamicMockController {

    private final MockService mockService;

    public DynamicMockController(MockService mockService) {
        this.mockService = mockService;
    }

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<?> handleAny(@RequestBody(required = false) String body,
                                       @RequestHeader Map<String, String> headers,
                                       HttpServletRequest request) {

        String path = request.getRequestURI();
        String method = request.getMethod();

        MockEndpoint endpoint = mockService.getMockEndpoint(path, method);
        if (endpoint != null) {
            return ResponseEntity
                    .status(endpoint.getStatusCode())
                    .header("Content-Type", "application/json")
                    .body(endpoint.getResponse());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
