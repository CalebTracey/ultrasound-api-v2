package com.ultrasound.app.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListItem {

    public enum MediaType {VIDEO,IMAGE};

    private String name;
    private String title;
    private String link;
    private Integer sequence;
    private EType type = EType.TYPE_ITEM;
    private MediaType mediaType;
    private Boolean graveStone;
//    public ListItem(String name, String title, String link, Integer sequence, Media) {
//        this.name = name;
//        this.title = title;
//        this.link = link;
//        this.sequence = sequence;
//    }
}
