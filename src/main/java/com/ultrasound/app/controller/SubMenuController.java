package com.ultrasound.app.controller;

import com.ultrasound.app.service.ClassificationServiceImpl;
import com.ultrasound.app.service.SubMenuServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api")
public class SubMenuController {

    @Autowired
    private SubMenuServiceImpl subMenuService;
    @Autowired
    private ClassificationServiceImpl classificationService;
//    @Autowired
//    private ItemServiceImpl itemService;

    @GetMapping("/subMenu/{id}")
    public ResponseEntity<?> subMenu(@PathVariable String id) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/subMenu/{id}").toUriString());
        return ResponseEntity.created(uri).body(subMenuService.getById(id));
    }
//
//    @PostMapping("/edit/subMenu/name/{classificationId}/{subMenuId}")
//    public ResponseEntity<?> editSubMenuName(@PathVariable String classificationId,
//                                             @PathVariable String subMenuId,
//                                             @RequestBody @NotNull Name name) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/edit/subMenu/name/{id}/{subMenuId}").toUriString());
//        Classification classification = classificationService.getById(classificationId);
//        SubMenu subMenu = subMenuService.getById(subMenuId);
//        return ResponseEntity.created(uri).body(subMenuService
//                .editName(classification, subMenu, subMenuId, name.getName()));
//    }

//    @PostMapping("/edit/subMenu/item/name/{id}")
//    public ResponseEntity<?> editSubmenuItemName(@PathVariable String id,
//                                                 @RequestBody @NotNull NewItemName item) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/edit/subMenu/item/name/{id}").toUriString());
//        return ResponseEntity.created(uri).body(subMenuService
//                .editItemName(id, item.getName(), item.getNewName(), item.getLink()));
//    }
//
//    @PostMapping("/delete-item/subMenu/{subMenuId}")
//    public ResponseEntity<?> deleteItemSubmenu(@PathVariable String subMenuId,
//                                               @RequestBody @NotNull ItemLink item) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/delete-item/subMenu/{subMenuId}").toUriString());
//        SubMenu subMenu = subMenuService.getById(subMenuId);
//        return ResponseEntity.created(uri).body(
//                itemService.deleteItem(subMenu, item.getLink(), item.getName(), subMenuId));
//    }
//
//    @PostMapping("/subMenu/create")
//    public ResponseEntity<?> createSubMenu(@RequestBody @NotNull NewEmptySubMenu emptySubMenu) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/subMenu/create").toUriString());
//        return ResponseEntity.created(uri).body(
//                subMenuService.createNew(emptySubMenu.getClassification(), emptySubMenu.getName()));
//    }
//
//    @DeleteMapping("/delete-data/subMenu/{classificationId}/{subMenuId}")
//    public ResponseEntity<?> deleteSubmenu(@PathVariable String classificationId,
//                                           @PathVariable String subMenuId) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/delete-data/subMenu/{id}").toUriString());
//        return ResponseEntity.created(uri).body(subMenuService
//                .deleteByIdClassification(classificationId, subMenuId));
//    }
//
//    @PostMapping("/edit/subMenu/move/{oldParentId}/{newParentId}")
//    public ResponseEntity<?> moveSubMenuItem(@PathVariable String oldParentId,
//                                             @PathVariable String newParentId,
//                                             @RequestBody ListItem item) {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/api/edit/subMenu/move/{oldParentId}/{newParentId}").toUriString());
//        return ResponseEntity.created(uri).body(subMenuService.moveSubMenuItem(oldParentId, newParentId, item));
//    }
}
