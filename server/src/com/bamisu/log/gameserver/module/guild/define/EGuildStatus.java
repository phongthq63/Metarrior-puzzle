package com.bamisu.log.gameserver.module.guild.define;

public enum  EGuildStatus {
    EXSIST(0),
    REMOVE(1);

    int id;

    EGuildStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


}
