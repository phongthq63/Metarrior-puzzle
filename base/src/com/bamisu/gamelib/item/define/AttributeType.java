package com.bamisu.gamelib.item.define;

public enum AttributeType {
    NUMBER(0),
    PERCENT(1);

    int value;

    AttributeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AttributeType fromValue(int value){
        for(AttributeType type : AttributeType.values()){
            if(type.value == value){
                return type;
            }
        }
        return null;
    }
}
