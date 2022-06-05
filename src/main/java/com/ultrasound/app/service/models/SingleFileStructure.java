package com.ultrasound.app.service.models;

import com.ultrasound.app.model.data.ListItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleFileStructure {

    private @NotNull String classification;
    private String subMenuName;
    private ListItem scan;
    private String link;
    private Boolean hasSubMenu;
    private Boolean hasScan;
}
