package com.bamisu.log.gameserver.datamodel.bag.entities;

public class MissionDetail {
    public long uid;
    public int lastTime;
    public int maxTime;

    public MissionDetail() {
    }

    public MissionDetail(long uid, int lastTime, int maxTime) {
        this.uid = uid;
        this.lastTime = lastTime;
        this.maxTime = maxTime;
    }
}
