package com.bamisu.log.gameserver.module.quest.config.entities;

import com.bamisu.gamelib.item.define.ResourceType;
import com.bamisu.log.gameserver.module.quest.defind.EMoneyQuestType;
import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuestVO {
    public String id;
    public String type;
    public int status;
    public List<List<ResourcePackage>> reward;
    public String rewardCondition;
    public String timeRefresh;
    public String description;
    public List<Long> condition;

    private List<ResourcePackage> readRewardQuestConfig(int current){
        if(current > reward.size() - 1){
            return new ArrayList<>();
        }
        return reward.get(current);
    }

    public List<ResourcePackage> readRewardQuestHeroEquip(int current){
        return readRewardQuestConfig(current).parallelStream().filter(obj -> ResourceType.fromID(obj.id).equals(ResourceType.WEAPON)).collect(Collectors.toList());
    }
    public List<ResourcePackage> readRewardQuestResource(int current){
        return readRewardQuestConfig(current).parallelStream().filter(obj -> !ResourceType.fromID(obj.id).equals(ResourceType.WEAPON)).collect(Collectors.toList());
    }
}
