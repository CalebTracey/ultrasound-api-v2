package com.ultrasound.app.controller;

import com.ultrasound.app.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ContentController  {

    private final ClassificationServiceImpl classificationService;
    private final SubMenuServiceImpl subMenuService;
    private final AppUserService userService;

    @GetMapping("/all")
    public String allAccess() {
        return "Total Members: " + (long) userService.all().size();
    }

    @GetMapping("/date")
    public String localDate() {
        LocalDate localDate = LocalDate.now();
        return localDate.getMonthOfYear() + " / " + localDate.getDayOfMonth() + " / " + localDate.getYear();
    }

    @DeleteMapping("/tables/clear")
    public ResponseEntity<?> deleteTables() {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/tables/clear").toUriString());
        subMenuService.deleteTableEntities();
        classificationService.deleteTableEntities();
        return ResponseEntity.created(uri).body("Database table entities deleted");
    }
}

