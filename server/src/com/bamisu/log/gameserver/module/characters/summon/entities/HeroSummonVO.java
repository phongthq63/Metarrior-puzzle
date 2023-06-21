package com.bamisu.log.gameserver.module.characters.summon.entities;

public class HeroSummonVO {
    public String idHero;
    public Short star;

    public static HeroSummonVO create(String idHero, int star) {
        HeroSummonVO heroSummonVO = new HeroSummonVO();
        heroSummonVO.idHero = idHero;
        heroSummonVO.star = (short) star;

        return heroSummonVO;
    }
}
