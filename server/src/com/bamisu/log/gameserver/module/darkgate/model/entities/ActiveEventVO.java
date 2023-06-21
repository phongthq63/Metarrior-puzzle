package com.bamisu.log.gameserver.module.darkgate.model.entities;

/**
 * Create by Popeye on 9:23 AM, 11/18/2020
 */
public class ActiveEventVO {
    public int id;
    public String hash;

    public ActiveEventVO() {
    }

    public ActiveEventVO(int id, String hash) {
        this.id = id;
        this.hash = hash;
    }
}
