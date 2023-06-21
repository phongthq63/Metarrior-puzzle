package com.bamisu.log.gameserver.module.mission.defind;

public enum EMissionStatus {
    DOING("0"),
    COMPLETED("1");

    String id;

    EMissionStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
