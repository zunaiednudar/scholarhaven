package com.example.scholarhaven.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @PostMapping("/echo")
    public Map<String, String> echo(@RequestBody Map<String, String> data) {
        return data;
    }
}