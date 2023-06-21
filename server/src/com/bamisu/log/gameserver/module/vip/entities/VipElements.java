package com.bamisu.log.gameserver.module.vip.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.List;

public class VipElements {
    public String time;
    public ResourcePackage price;
    public int points;
    public List<ResourcePackage> rewards;
    public List<Benefits> benefits;

    public VipElements(String time, ResourcePackage price, int points, List<ResourcePackage> rewards, List<Benefits> benefits) {
        this.time = time;
        this.price = price;
        this.points = points;
        this.rewards = rewards;
        this.benefits = benefits;
    }

    public VipElements() {
    }
}
