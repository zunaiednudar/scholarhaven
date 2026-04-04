
package com.example.scholarhaven.controller.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AdminApiIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    void testAdminEndpointBehavior() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is2xxSuccessful());
        System.out.println("/admin/dashboard endpoint accessible");
    }

    @Test
    void testApiEndpointBehavior() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().is2xxSuccessful());
        System.out.println("/api/admin/users endpoint accessible");
    }
}
