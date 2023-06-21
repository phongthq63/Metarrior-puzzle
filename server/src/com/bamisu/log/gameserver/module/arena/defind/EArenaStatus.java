package com.bamisu.log.gameserver.module.arena.defind;

public enum  EArenaStatus {
    OPEN("0"),
    CLOSE("1");

    private String id;

    EArenaStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
