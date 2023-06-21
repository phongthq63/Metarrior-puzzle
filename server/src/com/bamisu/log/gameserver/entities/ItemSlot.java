package com.bamisu.log.gameserver.entities;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Create by Popeye on 6:01 PM, 10/31/2019
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ItemSlot {
    WEAPON(0),      //Vũ khí
    NECKLACE(1),    //Dây chuyền
    HAT(2),         //Mũ
    ARMOR(3),       //Áo
    GLOVES(4),      //Tay
    PANT(5),        //Quần
    BELT(6),        //Đai lưng
    SHOES(7);       //Giầy

    int value;
    ItemSlot(int value){
        this.value = value;
    }

    @JsonProperty
    public int getValue(){
        return value;
    }


    @JsonCreator
    public static ItemSlot fromNode(JsonNode node) {
        if (!node.has("name"))
            return null;

        String name = node.get("name").asText();
        ItemSlot attr = ItemSlot.valueOf(name);
        attr.setValue(node.get("value").asInt());

        return attr;
    }


    @JsonProperty
    public String getName() {
        return name();
    }

    public void setValue(int value){
        this.value = value;
    }

    public String toString() {
        return String.valueOf(value);
    }

    public ItemSlot valueOf(int value) {
        for (ItemSlot itemSlot : ItemSlot.values()) {
            if (itemSlot.getValue() == value) {
                return itemSlot;
            }
        }

        return null;
    }
}
