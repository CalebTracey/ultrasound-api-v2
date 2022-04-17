package com.ultrasound.app.service;

import com.ultrasound.app.model.data.Classification;
import com.ultrasound.app.model.data.ListItem;
import com.ultrasound.app.model.data.SubMenu;
import com.ultrasound.app.payload.response.MessageResponse;

import java.util.List;

public interface SubMenuService {
    SubMenu save(SubMenu subMenu);
    SubMenu getById(String id);
    String insert(SubMenu subMenu);
    boolean existsById(String id);
//    MessageResponse moveSubMenuItem(String oldParentId, String newParentId, ListItem item);
    MessageResponse deleteById(String id);
    SubMenu createNew(String classificationId, String name);
//    MessageResponse deleteByIdClassification(String classificationId, String subMenuId);
//    MessageResponse editName(Classification classification, SubMenu subMenu, String id, String name);
//    MessageResponse editItemName(String id, String currentName, String name, String link);
    void deleteTableEntities();
    void clearGravestones();
    Boolean deleteOrphans(String subMenuId);
}
