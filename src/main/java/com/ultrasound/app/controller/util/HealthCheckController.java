package com.ultrasound.app.controller.util;

import org.springframework.web.bind.annotation.*;

@RestController
public class HealthCheckController {

    @GetMapping(path = "/")
    public String sayHello() {
        return "hello";
    }
}
