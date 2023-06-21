package com.bamisu.log.gameserver.module.characters.hero.entities;

import com.bamisu.log.gameserver.module.hero.entities.Stats;

public class CharacterStatsGrowVO {
    public String id;
    public Stats enhanceLevel = new Stats();
    public Stats enhanceStar = new Stats();


    public static CharacterStatsGrowVO create(CharacterStatsGrowVO config) {
        CharacterStatsGrowVO characterStatsGrowVO = new CharacterStatsGrowVO();
        characterStatsGrowVO.id = config.id;
        characterStatsGrowVO.enhanceLevel = new Stats(config.enhanceLevel);
        characterStatsGrowVO.enhanceStar = new Stats(config.enhanceStar);

        return characterStatsGrowVO;
    }
}
