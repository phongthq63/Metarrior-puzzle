package com.bamisu.log.gameserver.module.guild.define;

public enum EGuildVerificationType {
    AUTO_JOIN("0"),
    NEED_REQUEST("1"),
    CLOSE("2");

    String id;

    EGuildVerificationType(String id) {
        this.id = id;
    }

    public static EGuildVerificationType fromID(String id){
        for(EGuildVerificationType index : EGuildVerificationType.values()){
            if(index.getId().equals(id)){
                return index;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }
}
