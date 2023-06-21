package com.bamisu.log.gameserver.module.adventure.entities;

import java.util.List;

public class AdventureRewardConfig {

//    public int count;
    public List<FastRewardVO> listFastReward;
    public int timeReceive;
    public int maxTimeAFK;

    public AdventureRewardConfig() {
    }

    public AdventureRewardConfig(List<FastRewardVO> listFastReward, int maxTimeAFK, int timeReceive) {
//        this.count = count;
        this.listFastReward = listFastReward;
        this.maxTimeAFK = maxTimeAFK;
        this.timeReceive = timeReceive;
    }
}
