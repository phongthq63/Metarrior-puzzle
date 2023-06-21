package com.bamisu.log.gameserver.module.guild.define;

public enum EResourceGuildType {
    GUILD_EXP("RES1025", "Guild Experience"),
    GIFT_EXP("RES1026", "Gift Experience");

    EResourceGuildType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    String id;
    String name;

    public static EResourceGuildType fromID(String id){
        for (EResourceGuildType value : EResourceGuildType.values()) {
            if(value.getId().equalsIgnoreCase(id)) return value;
        }

        return null;
    }

    public String getId() {
        return id;
    }
}
