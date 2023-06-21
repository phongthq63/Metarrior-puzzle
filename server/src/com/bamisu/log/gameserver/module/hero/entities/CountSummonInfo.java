package com.bamisu.log.gameserver.module.hero.entities;

public class CountSummonInfo {
    public String idSummon;
    public int summon1;
    public int summon10;

    public static CountSummonInfo create(String idSummon) {
        CountSummonInfo data = new CountSummonInfo();
        data.idSummon = idSummon;
        data.summon1 = 0;
        data.summon10 = 0;

        return data;
    }
}
