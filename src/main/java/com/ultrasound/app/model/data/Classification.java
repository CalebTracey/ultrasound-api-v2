package com.ultrasound.app.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "classifications")
public class Classification {
    @Id
    private String _id;

    private @NotNull String name;
    private @NotNull Boolean hasSubMenu;
    private List<ListItem> listItems;
    private Map<String, String> subMenus;
    private EType type = EType.TYPE_CLASSIFICATION;
    private Boolean gravestone;

    public Classification(@NotNull String name, @NotNull Boolean hasSubMenu, List<ListItem> listItems, Map<String, String> subMenus) {
        this.name = name;
        this.hasSubMenu = hasSubMenu;
        this.listItems = listItems;
        this.subMenus = subMenus;
    }

    public Classification(@NotNull String name, @NotNull Boolean hasSubMenu, List<ListItem> listItems, Map<String, String> subMenus, EType type) {
        this.name = name;
        this.hasSubMenu = hasSubMenu;
        this.listItems = listItems;
        this.subMenus = subMenus;
        this.type = type;
    }
}
