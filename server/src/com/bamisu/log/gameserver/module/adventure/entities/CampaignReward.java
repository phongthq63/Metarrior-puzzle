package com.bamisu.log.gameserver.module.adventure.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

public class CampaignReward extends ResourcePackage {
    public int rate;

    public CampaignReward() {
    }

    public CampaignReward(String id, int amount, int rate) {
        super(id, amount);
        this.rate = rate;
    }
}
