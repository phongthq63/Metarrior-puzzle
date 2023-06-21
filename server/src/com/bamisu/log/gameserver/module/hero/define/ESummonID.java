package com.bamisu.log.gameserver.module.hero.define;

public enum ESummonID {
    BANNER_NORMAL("0"),
    BANNER_ELEMENT("1"),
    BANNER_KINGDOM("2"),
    BANNER_FRIEND("3"),
    SPECIAL_PURPLE_RANDOM_HERO_CHOOSE_KINGDOM("MON1010"),
    ALL_GREEN("green"),
    ALL_BLUE("blue"),
    ALL_PURPLE("purple"),
    GREEN_N_BLUE("greenNblue"),
    PURPLE_ELITE("purpleElite");

    public String getId() {
        return id;
    }

    String id;

    ESummonID(String id) {
        this.id = id;
    }

    public static ESummonID fromID(String id){
        for(ESummonID index : ESummonID.values()){
            if(index.id.equals(id)) return index;
        }
        return null;
    }
}
