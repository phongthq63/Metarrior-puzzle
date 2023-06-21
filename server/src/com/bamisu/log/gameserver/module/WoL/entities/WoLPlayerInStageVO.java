package com.bamisu.log.gameserver.module.WoL.entities;

public class WoLPlayerInStageVO {
    public long uid;
    public int stage;

    public WoLPlayerInStageVO(long uid, int stage) {
        this.uid = uid;
        this.stage = stage;
    }

    public WoLPlayerInStageVO() {
    }
}
