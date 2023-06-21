package com.bamisu.log.gameserver.datamodel.hero;

import com.bamisu.gamelib.skill.config.entities.SkillDesc;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.bamisu.log.gameserver.module.hero.exception.HeroConfigNotFoundException;
import com.bamisu.gamelib.skill.SkillConfigManager;
import com.bamisu.gamelib.skill.config.entities.BaseSkillInfo;
import com.bamisu.log.gameserver.module.skill.exception.SkillNotFoundException;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 3:29 PM, 12/23/2019
 * Thông tin skill của 1 hero instance
 */

public class HeroSkillModel extends DataModel {
    public String heroHash;
    public List<SkillInfo> skills;

    public static HeroSkillModel create(HeroModel heroModel) throws HeroConfigNotFoundException, SkillNotFoundException {
        HeroSkillModel heroSkillModel = new HeroSkillModel();
        heroSkillModel.heroHash = heroModel.hash;
        heroSkillModel.skills = new ArrayList<>();

        //get heroVO template
        HeroVO heroVO = null;
        switch (EHeroType.fromId(heroModel.type)) {
            case NFT:
                heroVO = CharactersConfigManager.getInstance().getHeroNFTConfig(heroModel.id);
                if (heroVO == null) {
                    throw new HeroConfigNotFoundException();
                }

                heroSkillModel.skills = SkillConfigManager.getInstance().generateSkillHeroNFT(heroVO.skill);
                break;
            case NORMAL:
                heroVO = CharactersConfigManager.getInstance().getHeroConfig(heroModel.id);
                if (heroVO == null) {
                    throw new HeroConfigNotFoundException();
                }

                heroSkillModel.skills = SkillConfigManager.getInstance().generateSkillHero(heroVO.skill);
                break;
        }
        if (heroSkillModel.skills == null) throw new SkillNotFoundException();

        return heroSkillModel;
    }

    public static HeroSkillModel create(String heroID) {
        HeroSkillModel heroSkillModel = new HeroSkillModel();
        heroSkillModel.skills = new ArrayList<>();
        //get heroVO template
        HeroVO heroVO = CharactersConfigManager.getInstance().getHeroConfig(heroID);
        if (heroVO == null) {
            try {
                throw new HeroConfigNotFoundException();
            } catch (HeroConfigNotFoundException e) {
                e.printStackTrace();
            }
        }

        heroSkillModel.skills = SkillConfigManager.getInstance().generateSkillHero(heroVO.skill);
        if (heroSkillModel.skills == null) {
            try {
                throw new SkillNotFoundException();
            } catch (SkillNotFoundException e) {
                e.printStackTrace();
            }
        }

        return heroSkillModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(heroHash, zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static HeroSkillModel getFromDB(HeroModel heroModel, Zone zone) {
        HeroSkillModel model = null;
        try {
            String str = (String) getModel(heroModel.hash, HeroSkillModel.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, HeroSkillModel.class);
                if (model != null) {
//                    model.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if (model == null) {
            try {
                model = create(heroModel);
                if (!model.saveToDB(zone)) return null;
            } catch (HeroConfigNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (SkillNotFoundException e) {
                e.printStackTrace();
                return null;
            }

        }
        return model;
    }
}
