package com.bamisu.gamelib.item.define;

public enum ResourceType {
    EXP("EXP"),
    MONEY("MON"),
    SPECIAL_ITEM("SPI"),
    HERO("T10"),
    FRAGMENT_HERO("FRA"),
    WEAPON("EQU"),
    STONE("GEM"),
    VIP("VIP"),
    SUMMON("SUM"),
    CELESTIAL_EQUIPMENT("CEL"),
    MAGE_EQUIPMENT("MAG"),
    RESOURCE("RES"),
    TOKEN("TOKEN");

    String type;

    ResourceType(String type){
        this.type = type;
    }

    public static ResourceType fromType(String type){
        for (ResourceType value : ResourceType.values()) {
            if(value.getType().equalsIgnoreCase(type)) return value;
        }

        return ResourceType.TOKEN;
    }
    public static ResourceType fromID(String id){
        for (ResourceType value : ResourceType.values()) {
            if(value.getType().equalsIgnoreCase(id.substring(0, 3))) return value;
        }

        return ResourceType.TOKEN;
    }

    public String getType(){return type;}

    public void setType(String type){this.type = type;}
}
