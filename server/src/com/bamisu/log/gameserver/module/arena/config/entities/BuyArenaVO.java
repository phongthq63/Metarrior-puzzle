package com.bamisu.log.gameserver.module.arena.config.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuyArenaVO {
    public ResourcePackage reward;
    public ResourcePackage cost;

    public List<ResourcePackage> readReward(){
        return new ArrayList<>(Collections.singleton(new ResourcePackage(reward)));
    }

    public List<ResourcePackage> readCost(){
        return new ArrayList<>(Collections.singleton(new ResourcePackage(cost)));
    }
}
