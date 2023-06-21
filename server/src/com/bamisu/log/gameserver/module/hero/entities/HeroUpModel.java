package com.bamisu.log.gameserver.module.hero.entities;

import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;

import java.util.List;

public class HeroUpModel {
    public HeroModel modelHeroUp;
    public List<HeroModel> modelHeroFission;

    public static HeroUpModel create(HeroModel modelHeroUp, List<HeroModel> modelHeroFission) {
        HeroUpModel heroUpModel = new HeroUpModel();
        heroUpModel.modelHeroUp = modelHeroUp;
        heroUpModel.modelHeroFission = modelHeroFission;

        return heroUpModel;
    }
}
