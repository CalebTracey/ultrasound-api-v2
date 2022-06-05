package com.ultrasound.app.controller;

import com.ultrasound.app.service.ClassificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ClassificationController {

    private final ClassificationServiceImpl classificationService;

    @GetMapping("/classifications")
    public ResponseEntity<?> classifications() {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/classifications").toUriString());
        return ResponseEntity.created(uri)
                .body(classificationService.all());
    }

    @GetMapping("/classifications/{id}")
    public ResponseEntity<?> getClassification(@PathVariable String id) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/classification/{id}").toUriString());
        return  ResponseEntity.created(uri).body(classificationService.getById(id));
    }
}
