package com.ultrasound.app.service;

import com.ultrasound.app.model.data.Classification;
import com.ultrasound.app.model.data.ListItem;
import com.ultrasound.app.model.data.SubMenu;
import com.ultrasound.app.payload.response.MessageResponse;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ClassificationService {
    void insert(Classification classification);
    MessageResponse createNew(String name);
    Boolean classificationExists(String classification);
    List<Classification> all();
    Classification getById(String id);
    Classification getByName(String name);
    String save(Classification classification);
    @NotNull Optional<List<ListItem>> allDatabaseScans();
    List<String> allDatabaseScanLinks();
    List<SubMenu> subMenuObjects(Map<String, String> subMenuMap);
    MessageResponse editName(Classification classification, String name);
    MessageResponse deleteById(String id);
    MessageResponse deleteSubMenu(String classificationId, String subMenuId);
    MessageResponse editItemName(String id, String currentName, String name, String link);
    void deleteTableEntities();
    void clearGravestones();
    String deleteOrphans();
}
