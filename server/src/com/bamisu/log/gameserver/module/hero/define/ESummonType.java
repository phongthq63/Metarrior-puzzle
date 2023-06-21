package com.bamisu.log.gameserver.module.hero.define;

public enum ESummonType {
    RANDOM("0"),
    DEPEND_ELEMENT("1"),
    DEPEND_KINGDOM("2"),
    CHOOSE_ELEMENT("3"),
    CHOOSE_KINGDOM("4");

    String id;

    ESummonType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static ESummonType fromID(String id){
        for (ESummonType value : ESummonType.values()) {
            if(value.id.equals(id)){
                return value;
            }
        }
        return null;
    }

    public boolean haveType(String id){
        if(ESummonType.fromID(id) != null){
            return true;
        }
        return false;
    }
}
