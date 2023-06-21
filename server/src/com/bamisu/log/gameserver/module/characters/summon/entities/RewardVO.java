package com.bamisu.log.gameserver.module.characters.summon.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

public class RewardVO extends ResourcePackage {

    public RewardVO() { }

    public RewardVO(ResourcePackage resource) {
        this.id = resource.id;
        this.amount = resource.amount;
    }

    @Override
    public String readType() {
        return super.readType();
    }

    public ResourcePackage toResourcePackage(){
        return new ResourcePackage(this.id, this.amount);
    }
}
