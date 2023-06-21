package com.bamisu.log.gameserver.module.tower.config.entities;

import com.bamisu.log.gameserver.module.campaign.config.entities.MonsterOnTeam;
import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.List;

public class TowerVO {
    public int floor;
    public List<MonsterOnTeam> enemy;
    public List<ResourcePackage> reward;
    public List<String> condition;
    public String terrain;
    public boolean bossMode;
    public String bbg;
}
