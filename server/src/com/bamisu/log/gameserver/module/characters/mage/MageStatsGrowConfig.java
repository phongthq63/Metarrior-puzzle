package com.bamisu.log.gameserver.module.characters.mage;

import com.bamisu.log.gameserver.module.hero.entities.Stats;

import java.util.Map;

public class MageStatsGrowConfig {
    public String method;
    public Map<String,Byte> params;
    public Stats enhanceLevel = new Stats();
}
