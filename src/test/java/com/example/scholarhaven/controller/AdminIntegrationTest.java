package com.example.scholarhaven.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@ActiveProfiles("test")
public class AdminIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    void setup() {
        // Build MockMvc manually (Spring Boot 4 no longer auto-configures)
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    void testServerIsRunning() throws Exception {
        var result = mockMvc.perform(get("/")).andReturn();
        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 400, "Unexpected status: " + status);
        System.out.println("✅ Server is running: HTTP " + status);
    }

    @Test
    void testPublicEndpointAccessible() throws Exception {
        var result = mockMvc.perform(get("/api/books")).andReturn();
        int status = result.getResponse().getStatus();
        assertTrue(status == 200 || status == 302, "Unexpected status: " + status);
        System.out.println("✅ Public endpoint /api/books responds with: HTTP " + status);
    }
}