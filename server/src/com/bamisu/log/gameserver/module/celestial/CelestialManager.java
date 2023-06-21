package com.bamisu.log.gameserver.module.celestial;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.log.gameserver.datamodel.celestial.UserCelestialModel;
import com.bamisu.gamelib.entities.Attr;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EConditionType;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.celestial.entities.*;
import com.bamisu.log.gameserver.module.characters.entities.Celestial;
import com.bamisu.log.gameserver.module.characters.hero.entities.CharacterStatsGrowVO;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.gamelib.item.entities.AttributeVO;
import com.bamisu.gamelib.item.entities.CelestialSlotVO;
import com.bamisu.log.gameserver.module.mage.MageManager;
import com.smartfoxserver.v2.entities.Zone;

import java.util.List;

public class CelestialManager {

    public static CelestialManager instance;

    public static CelestialManager getInstance(){
        if(instance == null){
            instance = new CelestialManager();
        }
        return instance;
    }

    public CelestialManager() {
    }

    /**
     * Get User Celestial Model
     */
    public UserCelestialModel getUserCelestialModel(Zone zone, long uid){
        UserCelestialModel userCelestialModel = UserCelestialModel.copyFromDBtoObject(uid, zone);
        if(userCelestialModel == null){
            return UserCelestialModel.createUserCelestialModel(uid, zone);
        }
        return userCelestialModel;
    }

    /**
     * Thay doi celestial dang su dung
     * @param uid
     * @param idCelestial
     * @return
     */
    public boolean changeCelestialUserCelestialModel(Zone zone, long uid, String idCelestial){
        UserCelestialModel userCelestialModel = getUserCelestialModel(zone, uid);
        return changeCelestialUserCelestialModel(zone, userCelestialModel, idCelestial);
    }
    public boolean changeCelestialUserCelestialModel(Zone zone, UserCelestialModel userCelestialModel, String idCelestial){
        userCelestialModel.cid = idCelestial;
        return userCelestialModel.saveToDB(zone);
    }

    /**
     * Kiem tra co the thay linh thu khong
     */
    public boolean haveUnlockCelestial(Zone zone, long uid, String idCelestial){
        return haveUnlockCelestial(getUserCelestialModel(zone, uid), idCelestial, zone);
    }
    public boolean haveUnlockCelestial(UserCelestialModel userCelestialModel, String idCelestial, Zone zone){
        return userCelestialModel.readListCelestialUnlocked(zone).contains(idCelestial);
    }

    /**
     * Kiem tra co the thay linh thu khong
     */
    public boolean checkCanUnlockCelestial(long uid, String idCelestial, Zone zone){
        UserCelestialModel userCelestialModel = getUserCelestialModel(zone, uid);
        return checkCanUnlockCelestial(userCelestialModel, idCelestial, zone);
    }
    public boolean checkCanUnlockCelestial(UserCelestialModel userCelestialModel, String idCelestial, Zone zone){
        CelestialVO celestialCf = CharactersConfigManager.getInstance().getCelestialConfig(idCelestial);
        if(celestialCf == null) return false;

        int level = BagManager.getInstance().getLevelUser(userCelestialModel.uid, zone);
        for(ResourcePackage condition : celestialCf.unlock){
            switch (EConditionType.fromID(condition.id)){
                case LEVEL_USER:
                    if(level < condition.amount) return false;
                    break;
                case RESOURCE_UNLOCK_CELESTIAL:
                    if(userCelestialModel.elysianCubes < condition.amount) return false;
                    break;
            }
        }
        return true;
    }

    public boolean unlockCelestial(UserCelestialModel userCelestialModel, String idCelestial, Zone zone){
        if(userCelestialModel.unlock.add(idCelestial)) return userCelestialModel.saveToDB(zone);
        return true;
    }

    /**
     * Tinh level cua Celestial
     */
    public LevelVO getLevelCelestial(Zone zone, long uid, String id){
        return new LevelVO(MageManager.getInstance().getUserMageModel(zone, uid).readLevel(zone), 0);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------- STATS CELESTIAL ------------------------------------------------*/
    /**
     * Luc chien
     */
    public int getPower(Stats stats){

        return (int) (stats.readHp() / 5 +
                stats.readStrength() * 1.25 + stats.readIntelligence() * 1.25 +
                stats.readArmor() * 2 + stats.readMagicResistance() * 2 +
                stats.readDexterity() * 20 +
                stats.readAgility() * 2.5 +
                stats.readElusiveness() * 2.5 +
                stats.readArmorPenetration() * 600 + stats.readMagicPenetration() * 600 +
                stats.readCrit() * 800 + stats.readCritDmg() * 150 +
                stats.readTenacity() * 3);
    }

    /**
     * Tinh chi so
     */
    public Stats getStatsCelestial(String idCelestial, UserCelestialModel userCelestialModel, Zone zone){
        //Chi so khi tang level
        Stats stats = getStatsCelestialModel(idCelestial, userCelestialModel, zone);

        return stats;
    }
    private Stats getStatsCelestialModel(String idCelestial, UserCelestialModel userCelestialModel, Zone zone){
        CelestialVO celestialCf = CharactersConfigManager.getInstance().getCelestialConfig(idCelestial);
        CharacterStatsGrowVO statsGrowConfig = CharactersConfigManager.getInstance().getCelestialStatsGrowConfig(idCelestial);
        int level = userCelestialModel.readLevelCelestial(zone);
        if(celestialCf == null || statsGrowConfig == null){
            return new Stats();
        }

        //Chi so co ban trong config
        Stats stats = new Stats(celestialCf);
        stats.attack = calculationStatsMage(statsGrowConfig, stats.readAttack(), Attr.ATTACK,  level - 1);
        stats.defensePenetration = calculationStatsMage(statsGrowConfig, stats.readDefensePenetration(), Attr.DEFENSE_PENETRATION, level - 1);
        stats.crit = calculationStatsMage(statsGrowConfig, stats.readCrit(), Attr.CRITICAL_CHANCE, level - 1);
        stats.critDmg = calculationStatsMage(statsGrowConfig, stats.readCritDmg(), Attr.CRITICAL_BONUS_DAMAGE, level - 1);

        return stats;
    }
    private final float calculationStatsMage(CharacterStatsGrowVO growCf, float baseCf, Attr attr, int level){
        switch (attr){
            case ATTACK:
                return baseCf + (baseCf * growCf.enhanceLevel.readAttack() * level);
            case DEFENSE_PENETRATION:
                return baseCf + (baseCf * growCf.enhanceLevel.readDefensePenetration() * level);
            case CRITICAL_CHANCE:
                return baseCf + (baseCf * growCf.enhanceLevel.readCrit() * level);
            case CRITICAL_BONUS_DAMAGE:
                return baseCf + (baseCf * growCf.enhanceLevel.readCritDmg() * level);
        }
        return 0;
    }
    public Stats getStatsItem(List<AttributeVO> listAttr){
        Stats stats = new Stats();
        for(AttributeVO attr : listAttr){
            switch (Attr.fromValue(attr.attr)){
                case ATTACK:
                    stats.attack = attr.attr;
                    break;
                case CRITICAL_CHANCE:
                    stats.crit = attr.param;
                    break;
                case CRITICAL_BONUS_DAMAGE:
                    stats.critDmg = attr.param;
                    break;
                case DEFENSE_PENETRATION:
                    stats.defensePenetration = attr.param;
                    break;
            }
        }

        return stats;
    }
}
