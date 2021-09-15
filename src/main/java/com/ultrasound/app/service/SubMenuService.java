package com.ultrasound.app.service;

import com.ultrasound.app.model.data.SubMenu;

import java.util.List;

public interface SubMenuService {

    String save(SubMenu subMenu);
    SubMenu getById(String id);
    String insert(SubMenu subMenu);
}
