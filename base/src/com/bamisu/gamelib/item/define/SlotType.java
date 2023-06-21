package com.bamisu.gamelib.item.define;

public enum SlotType {
    WEAPON(0),
    ACCESSORY(1),
    HEAD(2),
    CHEST(3),
    LEGS(4),
    WAIST(5),
    FEET(6);

    int value;

    SlotType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SlotType fromValue(int position){
        for(SlotType color : SlotType.values()){
            if(color.value == position){
                return color;
            }
        }
        return null;
    }
}
