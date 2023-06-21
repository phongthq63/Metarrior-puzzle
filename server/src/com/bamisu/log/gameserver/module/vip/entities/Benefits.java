package com.bamisu.log.gameserver.module.vip.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

public class Benefits {
    public String time;
    public ResourcePackage reward;

    public Benefits(String time, ResourcePackage reward) {
        this.time = time;
        this.reward = reward;
    }

    public Benefits() {
    }

    public Benefits(Benefits benefits) {
        ResourcePackage resourcePackage = new ResourcePackage(benefits.reward);
        this.reward = resourcePackage;
        this.time = benefits.time;
    }
}
