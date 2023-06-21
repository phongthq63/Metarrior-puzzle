package com.bamisu.log.gameserver.module.quest.config.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.List;
import java.util.stream.Collectors;

public class QuestChestVO {
    public String id;
    public short point;
    public String type;
    public List<ResourcePackage> reward;

    public List<ResourcePackage> readRewardChest(){
        return reward.parallelStream().map(ResourcePackage::new).collect(Collectors.toList());
    }
}
