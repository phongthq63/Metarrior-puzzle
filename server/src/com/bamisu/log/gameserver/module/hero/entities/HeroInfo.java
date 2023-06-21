package com.bamisu.log.gameserver.module.hero.entities;

import com.bamisu.log.gameserver.datamodel.hero.HeroSkillModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.smartfoxserver.v2.entities.Zone;

public class HeroInfo {
    public HeroModel heroModel;
    public HeroSkillModel heroSkillModel;

    public static HeroInfo create(HeroModel heroModel, Zone zone) {
        HeroInfo heroInfo = new HeroInfo();
        heroInfo.heroModel = heroModel;
        heroInfo.heroSkillModel = HeroSkillModel.getFromDB(heroModel, zone);

        return heroInfo;
    }

    public HeroInfo(){}

    public HeroInfo(HeroInfo heroInfo){
        this.heroModel = heroInfo.heroModel;
        this.heroSkillModel = heroInfo.heroSkillModel;
    }
}
