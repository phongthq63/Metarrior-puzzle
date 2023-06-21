package com.bamisu.log.gameserver.module.adventure.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.ArrayList;
import java.util.List;

public class LootRewardVO {
//    public List<ResourcePackage> loot;
    public List<LootVO> loot = new ArrayList<>();
    public List<CampaignReward> receive = new ArrayList<>();
    public List<CampaignReward> reward = new ArrayList<>();

    public LootRewardVO() {
    }

    public LootRewardVO(List<LootVO> loot, List<CampaignReward> receive, List<CampaignReward> reward) {
        this.loot = loot;
        this.receive = receive;
        this.reward = reward;
    }
}
