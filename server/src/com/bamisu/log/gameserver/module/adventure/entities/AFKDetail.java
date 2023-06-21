package com.bamisu.log.gameserver.module.adventure.entities;

/**
 * Create by Popeye on 11:24 AM, 12/28/2020
 */
public class AFKDetail {
    public long uid;
    public int rewardTime;
    public int maxTime;

    public AFKDetail() {
    }

    public AFKDetail(long uid, int rewardTime, int maxTime) {
        this.uid = uid;
        this.rewardTime = rewardTime;
        this.maxTime = maxTime;
    }
}
