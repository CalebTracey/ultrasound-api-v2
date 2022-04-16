package com.ultrasound.app.service;

import com.ultrasound.app.exceptions.ClassificationNotFoundException;
import com.ultrasound.app.exceptions.SubMenuNotFoundException;
import com.ultrasound.app.model.data.Classification;
import com.ultrasound.app.model.data.EType;
import com.ultrasound.app.model.data.SubMenu;
import com.ultrasound.app.repo.ClassificationRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ClassificationServiceImpl implements ClassificationService {

    private ClassificationRepo classificationRepo;
    private SubMenuService subMenuService;

    @Override
    public void insert(Classification classification) {
        classificationRepo.insert(classification);
    }

    @Override
    public Classification createNew(String name) {
        Map<String, String> subMenus = new TreeMap<>();
        Classification classification =
                new Classification(name, true, subMenus, EType.TYPE_CLASSIFICATION);
        classificationRepo.insert(classification);
        return classification;
    }

    @Override
    public Classification addNewSubMenu(String classificationId, String subMenuName) {
        Classification classification = getById(classificationId);
        SubMenu newSubMenu = subMenuService.createNew(classificationId,subMenuName);
        Map<String, String> classificationSubMenus = new TreeMap<>(classification.getSubMenus());
        classificationSubMenus.put(subMenuName, newSubMenu.get_id());
        classification.setSubMenus(classificationSubMenus);
        log.info("Added " + subMenuName + " to " + classification.getName());
        return save(classification);

    }

    @Override
    public Classification deleteSubMenu(String classificationId, String subMenuId) {
        Classification classification = getById(classificationId);

        try {
            SubMenu subMenu = subMenuService.getById(subMenuId);
            subMenuService.deleteById(subMenu.get_id());
            // need to also remove the reference from the classification
            classification.getSubMenus().remove(subMenu.getName());
        } catch (SubMenuNotFoundException ex) {
            log.error("Tried to remove subMenuId {} from classification {}. Submenu not found.", subMenuId,classificationId);
            return classification;
        }

        return save(classification);
    }

    @Override
    public Boolean classificationExists(String classification) {
        return classificationRepo.existsByName(classification);
    }

    public List<Classification> all() {
        return classificationRepo.findAll();
    }

    @Override
    public Classification getById(String id) {
        log.info("Classification ID: {}", id);
        return classificationRepo.findById(id)
                .orElseThrow(() -> new ClassificationNotFoundException(id));
    }

    @Override
    public Classification getByName(String name) {
        return classificationRepo.findByName(name)
                .orElseThrow(() -> new ClassificationNotFoundException(name));
    }

    public Classification save(@NotNull Classification classification) {
        log.info("Saving classification: {}", classification.getName());
        return classificationRepo.save(classification);
    }



    @Override
    public List<SubMenu> subMenuObjects(@NotNull Map<String, String> subMenuMap) {
        List<String> subMenuIds = new ArrayList<>(new LinkedHashSet<>(subMenuMap.values()));
        return subMenuIds.stream().map(id -> subMenuService.getById(id)).collect(Collectors.toList());
    }

    @Override
    public void deleteTableEntities() {
        classificationRepo.deleteAll();
    }

    @Override
    public void clearGravestones() {
        List<Classification> classifications = all();

        classifications.forEach(classification -> {
            classification.setGravestone(true);
            save(classification);
        });

        subMenuService.clearGravestones();
    }

    @Override
    public String deleteOrphans() {
        StringBuilder builder = new StringBuilder();

        // now iterate through the DB entries, deleting any DB records for files we didn't find in S3.
        List<Classification> classifications = all();
        classifications.forEach(classification -> {

            HashMap<String,String> subMenusToRetain = new HashMap<>(classification.getSubMenus());
            classification.getSubMenus().values().forEach(subMenuId -> {
                try {
                    String subMenuName = subMenuService.getById(subMenuId).getName();
                    if (subMenuService.deleteOrphans(subMenuId)) {
                        // the subMenu was deleted so update the classification
                        // can't delete it now - concurrent modification
                        subMenusToRetain.remove(subMenuName);
                    }
                } catch (SubMenuNotFoundException ex) {
                    builder.append("Bad submenu id for classification ").append(classification).append(" is ").append(subMenuId);
                }
            });

            // remove unused subMenus
            classification.setSubMenus(subMenusToRetain);
            save(classification);

            // delete the classification if it hasn't been touched
            if (classification.getGravestone()) {
                deleteById(classification.get_id());
            }
        });

        return builder.toString();
    }

    @Override
    public void deleteById(String id) {
        AtomicInteger count = new AtomicInteger(0);
        Classification classification = getById(id);
        String name = classification.getName();
        Map<String, String> subMenus = classification.getSubMenus();
        subMenus.values().forEach(subMenuId -> {
            log.info("Deleting Submenu: {}", subMenuId);
            if (subMenuService.existsById(subMenuId)) {
                subMenuService.deleteById(subMenuId);
                count.getAndIncrement();
            }
        });
        log.info("Deleting Classification {} and {} submenus", name, count);
        classificationRepo.deleteById(id);
    }
}