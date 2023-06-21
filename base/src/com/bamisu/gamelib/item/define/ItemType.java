package com.bamisu.gamelib.item.define;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ItemType {
    EQUIP(1),           //Equipment
    AVATAR(2),
    BANNER(3),
    BORDER(4),
    FRAGMENT(5),
    STONE(6),
    MONEY(7),
    SPECIAL_ITEM(8);
    int id;

    ItemType(int id) {
        this.id = id;
    }

    ItemType(){

    }

    @JsonProperty
    public int getId() {
        return id;
    }


    public void setId(int id){
        this.id = id;
    }

}
