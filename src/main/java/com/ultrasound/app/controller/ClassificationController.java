package com.ultrasound.app.controller;

import com.ultrasound.app.service.ClassificationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api")
public class ClassificationController {

    @Autowired
    private ClassificationServiceImpl classificationService;
//    @Autowired
//    private ItemServiceImpl itemService;

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

//    @PostMapping("/edit/classification/name/{id}")
//    public ResponseEntity<?> editClassificationName(@PathVariable String id,
//                                                    @RequestBody @NotNull Name name) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/edit/classification/name/{id}").toUriString());
//        Classification classification = classificationService.getById(id);
//        return ResponseEntity.created(uri).body(classificationService.editName(classification, name.getName()));
//
//    }
//
//    @PostMapping("/edit/classification/item/name/{id}")
//    public ResponseEntity<?> editClassificationItemName(@PathVariable String id,
//                                                        @RequestBody @NotNull NewItemName item) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/edit/classification/item/name/{id}").toUriString());
//        return ResponseEntity.created(uri).body(classificationService
//                .editItemName(id, item.getName(), item.getNewName(), item.getLink()));
//    }
//
//    @DeleteMapping("/delete-data/classification/{id}")
//    public ResponseEntity<?> deleteClassification(@PathVariable String id) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/delete-data/classification/{id}").toUriString());
//        return ResponseEntity.created(uri).body(classificationService
//                .deleteById(id));
//    }
//
//    @PostMapping("/classification/create")
//    public ResponseEntity<?> createClassification(@RequestBody @NotNull NewEmptyClassification emptyClassification) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/classification/create").toUriString());
//        return ResponseEntity.created(uri).body(classificationService.createNew(emptyClassification.getName()));
//    }
//
//    @PostMapping("/delete-item/classification/{classificationId}")
//    public ResponseEntity<?> deleteItemClassification(@PathVariable String classificationId,
//                                                      @RequestBody @NotNull ItemLink item) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/delete-item/classification/{classificationId}").toUriString());
//        Classification classification = classificationService.getById(classificationId);
//        return ResponseEntity.created(uri).body(
//                itemService.deleteItem(classification, item.getLink(), item.getName(), classificationId));
//    }
}
