package com.ultrasound.app.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "subMenus")
public class SubMenu {

    @Id
    private String _id;

    private String classification;
    private String name;
    private List<ListItem> itemList;
    private EType type = EType.TYPE_SUB_MENU;
    private Boolean gravestone;

    public SubMenu(String name, List<ListItem> itemList) {
        this.name = name;
        this.itemList = itemList;
    }

    public SubMenu(String name, List<ListItem> itemList, EType type) {
        this.name = name;
        this.itemList = itemList;
        this.type = type;
    }

    public SubMenu(String classification, String name, List<ListItem> itemList) {
        this.classification = classification;
        this.name = name;
        this.itemList = itemList;
    }
}
