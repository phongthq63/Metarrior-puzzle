package com.bamisu.log.gameserver.datamodel.hero;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.hero.entities.CharacterStatsGrowVO;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.smartfoxserver.v2.entities.Zone;

/**
 * Created by Quach Thanh Phong
 * On 4/4/2022 - 8:20 PM
 */
public class HeroBaseStatsModel extends DataModel {

    public String hash;
    public String heroId;
    public HeroVO baseStats;
    public CharacterStatsGrowVO growStats;




    private void init() {
        this.baseStats = CharactersConfigManager.getInstance().getHeroNFTStatsConfig(this.heroId);
        this.growStats = CharactersConfigManager.getInstance().getHeroNFTStatsGrowConfig(this.heroId);
    }

    public static HeroBaseStatsModel create(String hash, String heroId, Zone zone){
        HeroBaseStatsModel heroBaseStatsModel = new HeroBaseStatsModel();
        heroBaseStatsModel.hash = hash;
        heroBaseStatsModel.heroId = heroId;
        heroBaseStatsModel.init();
        heroBaseStatsModel.saveToDB(zone);

        return heroBaseStatsModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(hash, zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static HeroBaseStatsModel copyFromDBtoObject(String hashHero, Zone zone) {
        HeroBaseStatsModel pInfo = null;
        try {
            String str = (String) getModel(hashHero, HeroBaseStatsModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, HeroBaseStatsModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }

    public static HeroBaseStatsModel copyFromDBtoObject(String hashHero, String heroId, Zone zone) {
        HeroBaseStatsModel pInfo = null;
        try {
            String str = (String) getModel(hashHero, HeroBaseStatsModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, HeroBaseStatsModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if (pInfo == null) {
            pInfo = create(hashHero, heroId, zone);
        }

        return pInfo;
    }
}
