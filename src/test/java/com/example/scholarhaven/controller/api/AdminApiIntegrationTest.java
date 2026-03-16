package com.example.scholarhaven.controller.api;

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
public class AdminApiIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    void testAdminEndpointBehavior() throws Exception {
        var result = mockMvc.perform(get("/admin/dashboard")).andReturn();
        int status = result.getResponse().getStatus();
        assertTrue(status == 200 || status == 302 || status == 401 || status == 403,
                "Unexpected status: " + status);
        System.out.println("✅ /admin/dashboard responds with: HTTP " + status);
    }

    @Test
    void testApiEndpointBehavior() throws Exception {
        var result = mockMvc.perform(get("/api/admin/users")).andReturn();
        int status = result.getResponse().getStatus();
        assertTrue(status == 200 || status == 302 || status == 401 || status == 403,
                "Unexpected status: " + status);
        System.out.println("✅ /api/admin/users responds with: HTTP " + status);
    }
}