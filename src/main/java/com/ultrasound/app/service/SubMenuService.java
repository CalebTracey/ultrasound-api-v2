package com.ultrasound.app.service;

import com.ultrasound.app.model.data.Classification;
import com.ultrasound.app.model.data.ListItem;
import com.ultrasound.app.model.data.SubMenu;
import com.ultrasound.app.payload.response.MessageResponse;

import java.util.List;

public interface SubMenuService {
    SubMenu save(SubMenu subMenu);
    SubMenu getById(String id);
    boolean existsById(String id);
    SubMenu createNew(String classificationId, String name);
    void deleteTableEntities();
    void clearGravestones();
    Boolean deleteOrphans(String subMenuId);

    void deleteById(String subMenuId);
}
