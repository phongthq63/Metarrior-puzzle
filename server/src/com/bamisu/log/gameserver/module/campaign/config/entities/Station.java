package com.bamisu.log.gameserver.module.campaign.config.entities;

import com.bamisu.log.gameserver.module.adventure.entities.LootRewardVO;
import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 3:13 PM, 2/5/2020
 */
public class Station {
    public List<MonsterOnTeam> enemy = new ArrayList<>();
    public LootRewardVO reward = new LootRewardVO();
    public List<StarRewardVO> complete = new ArrayList<>();
    public int number;
    public String name;
    public List<String> condition;
    public String terrain;
    public boolean bossMode;
    public String bbg;

    public List<ResourcePackage> readRewardComplete(int oldStar, int newStar){
        if(oldStar >= newStar)return new ArrayList<>();

        List<ResourcePackage> reward = new ArrayList<>();
        for(StarRewardVO starCf : complete){
            if(starCf.star > oldStar && starCf.star <= newStar){
                reward.addAll(starCf.reward);
            }
        }
        return reward;
    }
}
