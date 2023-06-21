package com.bamisu.log.gameserver.module.WoL.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

public class WoLContentVO {
    public ResourcePackage reward1;
    public ResourcePackage reward2;
    public int condition;

    public WoLContentVO(ResourcePackage reward1, ResourcePackage reward2, int condition) {
        this.reward1 = reward1;
        this.reward2 = reward2;
        this.condition = condition;
    }

    public WoLContentVO(){}
}
