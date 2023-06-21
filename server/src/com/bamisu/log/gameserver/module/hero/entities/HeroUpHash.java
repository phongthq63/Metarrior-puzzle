package com.bamisu.log.gameserver.module.hero.entities;

import java.util.List;

public class HeroUpHash {
    public String hashHeroUp;
    public List<String> hashHeroFission;

    public static HeroUpHash create(String hashHeroUp, List<String> hashHeroFission) {
        HeroUpHash heroUpHash = new HeroUpHash();
        heroUpHash.hashHeroUp = hashHeroUp;
        heroUpHash.hashHeroFission = hashHeroFission;

        return heroUpHash;
    }
}
