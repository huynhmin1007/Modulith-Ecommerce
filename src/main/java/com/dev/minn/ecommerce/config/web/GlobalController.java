package com.dev.minn.ecommerce.config.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalController {

    @GetMapping
    public String connect() {
        return "Connected";
    }
}
