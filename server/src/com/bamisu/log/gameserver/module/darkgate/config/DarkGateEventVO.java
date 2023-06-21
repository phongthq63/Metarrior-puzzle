package com.bamisu.log.gameserver.module.darkgate.config;

/**
 * Create by Popeye on 10:31 AM, 11/12/2020
 */
public class DarkGateEventVO {
    public int id;
    public String name;

    public DarkGateEventVO() {
    }

    public DarkGateEventVO(int id, String name, int activeTime) {
        this.id = id;
        this.name = name;
    }
}
