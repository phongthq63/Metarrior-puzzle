package com.bamisu.gamelib.item.define;

public enum HeroResource {
    KINGDOM("K"),
    ELEMENT("E");

    String type;

    HeroResource(String type){
        this.type = type;
    }

    HeroResource(){}

    public static HeroResource fromType(String type){
        for (HeroResource value : HeroResource.values()) {
            if(value.getType().equalsIgnoreCase(type)) return value;
        }

        return null;
    }

    public static HeroResource fromID(String id){
        for (HeroResource value : HeroResource.values()) {
            if(value.getType().equalsIgnoreCase(id.substring(0, 1))) return value;
        }

        return null;
    }

    public String getType(){return type;}

    public void setType(String type){this.type = type;}
}
