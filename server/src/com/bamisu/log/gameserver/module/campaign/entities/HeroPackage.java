package com.bamisu.log.gameserver.module.campaign.entities;

import com.bamisu.log.gameserver.module.characters.entities.Hero;

import java.util.List;

/**
 * Create by Popeye on 3:01 PM, 2/17/2020
 */
public class HeroPackage {
    public List<Hero> heroList;

    public HeroPackage() {
    }

    public HeroPackage(List<Hero> team) {
        this.heroList = team;
    }
}
