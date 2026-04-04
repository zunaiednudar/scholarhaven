package com.example.scholarhaven.controller;

import com.example.scholarhaven.entity.Category;
import com.example.scholarhaven.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class PublicControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        
        categoryRepository.deleteAll();
        
        
        Category category1 = new Category();
        category1.setName("Fiction_Test_" + System.currentTimeMillis());
        category1.setDescription("Fictional books");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("NonFiction_Test_" + System.currentTimeMillis());
        category2.setDescription("Non-fictional books");
        categoryRepository.save(category2);
    }

    @Test
    void testGetCategories_ShouldReturnAllCategories() throws Exception {
        mockMvc.perform(get("/public/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetCategories_WhenNoCategories_ShouldReturnEmptyArray() throws Exception {
        categoryRepository.deleteAll();
        
        mockMvc.perform(get("/public/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}