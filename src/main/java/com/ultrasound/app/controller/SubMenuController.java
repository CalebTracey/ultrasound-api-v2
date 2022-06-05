package com.ultrasound.app.controller;

import com.ultrasound.app.service.ClassificationServiceImpl;
import com.ultrasound.app.service.SubMenuServiceImpl;
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
public class SubMenuController {

    private final SubMenuServiceImpl subMenuService;

    @GetMapping("/subMenu/{id}")
    public ResponseEntity<?> subMenu(@PathVariable String id) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/subMenu/{id}").toUriString());
        return ResponseEntity.created(uri).body(subMenuService.getById(id));
    }
}
