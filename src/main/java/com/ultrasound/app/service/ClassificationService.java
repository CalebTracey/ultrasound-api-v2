package com.ultrasound.app.service;

import com.ultrasound.app.model.data.Classification;
import com.ultrasound.app.model.data.SubMenu;
import com.ultrasound.app.payload.response.MessageResponse;

import java.util.List;
import java.util.Map;

public interface ClassificationService {
    void createNew(String name);
    Classification addNewSubMenu(String classificationId, String subMenuName);
    Boolean classificationExists(String classification);
    List<Classification> all();
    Classification getById(String id);
    Classification getByName(String name);
    Classification save(Classification classification);
    void deleteById(String id);
    void deleteTableEntities();
    void clearGravestones();
    String deleteOrphans();
}
