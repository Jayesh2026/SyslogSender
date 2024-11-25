package com.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

    
    @GetMapping("/hello")
    public String hello() {
        logger.info("Hello World");
        return "Hello World";
    }

    @GetMapping
    public String hello(@RequestParam("name") String name) {
        logger.info("Say hello to {}", name);
        return "Hello " + name;
    }
}
