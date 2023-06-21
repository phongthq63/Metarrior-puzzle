package com.bamisu.log.gameserver.module.arena.config.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.List;
import java.util.stream.Collectors;

public class RewardRankArenaVO {
    public List<Integer> top;
    public List<Integer> range;
    public List<ResourcePackage> reward;

    public List<ResourcePackage> readReward(){
        return reward.parallelStream().map(ResourcePackage::new).collect(Collectors.toList());
    }
}
