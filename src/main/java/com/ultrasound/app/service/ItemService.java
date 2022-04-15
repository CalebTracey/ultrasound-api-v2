package com.ultrasound.app.service;

import com.ultrasound.app.model.data.Classification;
import com.ultrasound.app.model.data.ListItem;
import com.ultrasound.app.model.data.SubMenu;
import com.ultrasound.app.payload.response.MessageResponse;

import java.util.List;

public interface ItemService {
    MessageResponse deleteItem(Classification classification, String title, String name, String parentId);
    MessageResponse deleteItem(SubMenu subMenu, String title, String name, String parentId);
    ListItem findByLink(List<ListItem> listItems, String link, String name, String subName, String parentType);
    List<ListItem> removeItemFromList(List<ListItem> listItems, String link);
    ListItem editTitle(ListItem item, String classificationName, String subMenuName, String newName);
    ListItem editTitle(ListItem item, String classificationName, String newName);
}
