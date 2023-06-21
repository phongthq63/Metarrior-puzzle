package com.bamisu.log.gameserver.module.guild.define;

public enum EGuildSetting {
    NOTICE("0"),
    POWER_REQUEST("1"),
    LANGUAGE("2"),
    VERIFICATION("3"),
    GUILD_MASTER("4"),
    GUILD_VICE("5"),
    GUILD_LEADER("6"),
    AVATAR("7");

    String id;

    public String getId() {
        return id;
    }

    EGuildSetting(String id) {
        this.id = id;
    }

    public static EGuildSetting fromID(String id){
        for(EGuildSetting index : EGuildSetting.values()){
            if(index.id.equals(id)){
                return index;
            }
        }
        return null;
    }
}
