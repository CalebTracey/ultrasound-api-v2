package com.ultrasound.app.controller;

import com.ultrasound.app.aws.S3ServiceImpl;
import com.ultrasound.app.controller.util.ItemLink;
import com.ultrasound.app.controller.util.Name;
import com.ultrasound.app.controller.util.NewEmptySubMenu;
import com.ultrasound.app.controller.util.NewItemName;
import com.ultrasound.app.model.data.Classification;
import com.ultrasound.app.model.data.SubMenu;
import com.ultrasound.app.service.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api")
public class ContentController  {

    @Autowired
    private ClassificationServiceImpl classificationService;
    @Autowired
    private SubMenuServiceImpl subMenuService;
//    @Autowired
//    private ItemServiceImpl itemService;
    @Autowired
    private AppUserService userService;
//    @Autowired
//    private S3ServiceImpl s3Repository;

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

