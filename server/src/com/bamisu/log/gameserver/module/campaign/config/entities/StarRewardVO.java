package com.bamisu.log.gameserver.module.campaign.config.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.List;

public class StarRewardVO {
    public byte star;
    public List<ResourcePackage> reward;

    public StarRewardVO() {
    }

    public StarRewardVO(byte star, List<ResourcePackage> reward) {
        this.star = star;
        this.reward = reward;
    }
}
