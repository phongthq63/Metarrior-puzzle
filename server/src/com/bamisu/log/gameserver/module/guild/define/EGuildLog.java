package com.bamisu.log.gameserver.module.guild.define;

public enum EGuildLog {
    CREATE_GUILD("gl4"),
    MEMBER_JOIN_GUILD("gl0"),
    LEAVE_GUILD("gl2"),
    GET_GIFT_GUILD_DEPEND_LEVEL("gl1"),
    UP_OFFICE_VICE("gl3"),
    DOWN_OFFICE_MEMBER("gl6"),
    CHANGE_LEVEL_GUILD("gl5"),
    UP_OFFICE_MASTER("gl7"),
    UP_OFFICE_LEAD("gl8"),
    KICK_FROM_GUILD("gl9");

    String id;

    public String getId() {
        return id;
    }

    EGuildLog(String id) {
        this.id = id;
    }
}
