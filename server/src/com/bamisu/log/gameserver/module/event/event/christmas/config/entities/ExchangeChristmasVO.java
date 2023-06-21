package com.bamisu.log.gameserver.module.event.event.christmas.config.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.List;
import java.util.stream.Collectors;

public class ExchangeChristmasVO {
    public String id;
    public List<ResourcePackage> cost;
    public List<ResourcePackage> reward;
    public int buy;

    public List<ResourcePackage> readCost(){
        return cost.parallelStream().
                map(obj -> new ResourcePackage(obj.id, -obj.amount)).
                collect(Collectors.toList());
    }

    public List<ResourcePackage> readReward(){
        return reward.parallelStream().
                map(obj -> new ResourcePackage(obj.id, obj.amount)).
                collect(Collectors.toList());
    }
}
