package com.bamisu.log.gameserver.datamodel.hunt.entities;

import com.bamisu.log.gameserver.module.campaign.config.entities.MonsterOnTeam;

public class MonsterInfo {
    public MonsterOnTeam monster;
    public float currentHp;

    public static MonsterInfo create(MonsterOnTeam monster) {
        MonsterInfo info = new MonsterInfo();
        info.monster = monster;
        info.currentHp = 100;

        return info;
    }
}
