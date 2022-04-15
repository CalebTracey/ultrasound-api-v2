package com.ultrasound.app.service;

import com.ultrasound.app.exceptions.ClassificationNotFoundException;
import com.ultrasound.app.exceptions.SubMenuNotFoundException;
import com.ultrasound.app.exceptions.UpdateDatabaseException;
import com.ultrasound.app.model.data.Classification;
import com.ultrasound.app.model.data.EType;
import com.ultrasound.app.model.data.ListItem;
import com.ultrasound.app.model.data.SubMenu;
import com.ultrasound.app.payload.response.MessageResponse;
import com.ultrasound.app.repo.ClassificationRepo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class ClassificationServiceImpl implements ClassificationService {

    @Autowired
    private ClassificationRepo classificationRepo;
    @Autowired
    private SubMenuServiceImpl subMenuService;
    @Autowired
    private ItemServiceImpl itemService;

    @Override
    public void insert(Classification classification) {
        classificationRepo.insert(classification);
    }

    @Override
    public MessageResponse createNew(String name) {
        Map<String, String> subMenus = new TreeMap<>();
        Classification classification =
                new Classification(name, true, new ArrayList<>(), subMenus, EType.TYPE_CLASSIFICATION);
        classificationRepo.insert(classification);
        return new MessageResponse(name + " created");
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

    public String save(@NotNull Classification classification) {
        log.info("Saving classification: {}", classification.getName());
        return classificationRepo.save(classification).get_id();
    }

    @Override
    public @NotNull Optional<List<ListItem>> allDatabaseScans() {
        List<Classification> allClassifications = classificationRepo.findAll();
        List<ListItem> scanList = new ArrayList<>();
        allClassifications.forEach(classification -> {
            if (classification.getHasSubMenu()) {
                List<SubMenu> subMenus = classification.getSubMenus().values().stream().map(
                        id -> subMenuService.getById(id)).collect(Collectors.toList());
                subMenus.forEach(subMenu -> scanList.addAll(subMenu.getItemList()));
            }
            scanList.addAll(classification.getListItems());
        });
        log.info("Found " + scanList.size() + " scans in the database");
        return Optional.of(new ArrayList<>(scanList));
    }


    @Override
    public List<String> allDatabaseScanLinks() {
        List<ListItem> allItems = allDatabaseScans().orElseThrow(
                () -> new UpdateDatabaseException("Problem fetching all scan files from database"));
        return allItems.stream().map(ListItem::getLink).collect(Collectors.toList());
    }

    @Override
    public List<SubMenu> subMenuObjects(@NotNull Map<String, String> subMenuMap) {
        List<String> subMenuIds = new ArrayList<>(new LinkedHashSet<>(subMenuMap.values()));
        return subMenuIds.stream().map(id -> subMenuService.getById(id)).collect(Collectors.toList());
    }

    @Override
    public MessageResponse editName(@NotNull Classification classification, String name) {
        String origName = classification.getName();
        classification.setName(name);
        log.info("Changing Classification name {} to {}",origName, name);
        classificationRepo.save(classification);
        return new MessageResponse("Changed Classification name " + origName + " to " + name);
    }

    @Override
    public MessageResponse editItemName(String id, String currentName, String name, String link) {
        Classification classification = getById(id);
        String className = classification.getName();
        List<ListItem> listItems = classification.getListItems();

        ListItem item = itemService.findByLink(listItems, link, name, "classification", className);
        List<ListItem> itemList;
        if (listItems.size() > 1) {
            itemList = itemService.removeItemFromList(listItems, link);
        } else {
            itemList = new ArrayList<>();
            itemList.add(item);
        }
        item.setName(name);
        item.setTitle(classification.getName() + " " + name);
        itemList.add(item);
        classification.setListItems(new ArrayList<>(new LinkedHashSet<>(itemList)));
        save(classification);
        return new MessageResponse("Saved " + currentName + " as " + name + " in " + className);
    }

    @Override
    public void deleteTableEntities() {
        classificationRepo.deleteAll();
    }

    @Override
    public void clearGravestones() {
        for (final ListIterator<Classification> i = all().listIterator(); i.hasNext();) {
            final Classification c = i.next();
            c.setGravestone(true);
            save(c);
        }
    }

    @Override
    public String deleteOrphans() {
        StringBuilder builder = new StringBuilder();

        // now iterate through the DB entries, deleting any DB records for files we didn't find in S3.
        List<Classification> classifications = all();
        classifications.forEach(classification -> {

            classification.getSubMenus().values().forEach(subMenuId -> {
                try {
                    subMenuService.deleteOrphans(subMenuId);
                } catch (SubMenuNotFoundException ex) {
                    builder.append("Bad submenu id for classification " + classification + " is " + subMenuId);
                }

            });
            // delete the classification if it hasn't been touched
            if (classification.getGravestone()) {
                deleteById(classification.get_id());
            }
        });

        return builder.toString();
    }

    @Override
    public MessageResponse deleteById(String id) {
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
        return new MessageResponse("Deleted " + name + " and " + count + " submenus");
    }

    @Override
    public MessageResponse deleteSubMenu(String classificationId, String subMenuId) {
        Classification classification = getById(classificationId);
        String name = classification.getName();
        String subName = subMenuService.getById(subMenuId).getName();
        Map<String, String> subMenus = classification.getSubMenus();
        subMenus.remove(subName, subMenuId);
        classification.setSubMenus(subMenus);
        save(classification);

        return new MessageResponse("Deleted " + subName + " and updated the classification " +
                name);
    }
}