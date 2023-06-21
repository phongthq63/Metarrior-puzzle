package com.bamisu.log.gameserver.module.guild.define;

public enum EGuildGiftType {
    CREATE("0"),
    DAILY("1"),
    UP_LEVEL_GIFT("2"),
    BUY("3");

    String id;

    public String getId() {
        return id;
    }

    EGuildGiftType(String id) {
        this.id = id;
    }

    public static EGuildGiftType fromID(String id){
        for(EGuildGiftType index : EGuildGiftType.values()){
            if(index.id.equals(id)){
                return index;
            }
        }
        return null;
    }
}
