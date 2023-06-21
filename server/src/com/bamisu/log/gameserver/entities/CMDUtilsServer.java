package com.bamisu.log.gameserver.entities;

import com.bamisu.gamelib.entities.Attr;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Create by Popeye on 3:09 PM, 6/19/2020
 */
public class CMDUtilsServer {
    /**
     * Stats hero To SFSObject
     */
    public static ISFSObject statsHeroToSFSObject(Stats stats){
        ISFSObject objReturn = new SFSObject();
        objReturn.putInt(Params.POWER, HeroManager.getInstance().getPower(stats));
        objReturn.putFloat(Params.ModuleChracter.HP, Math.round(stats.readHp()));
        objReturn.putFloat(Params.ModuleChracter.STRENGTH, Math.round(stats.readStrength()));
        objReturn.putFloat(Params.ModuleChracter.INTELLIGENCE, Math.round(stats.readIntelligence()));
        objReturn.putFloat(Params.ModuleChracter.DEXTERITY, Math.round(stats.readDexterity()));
        objReturn.putFloat(Params.ModuleChracter.ARMOR, Math.round(stats.readArmor()));
        objReturn.putFloat(Params.ModuleChracter.MAGIC_RESISTANCE, Math.round(stats.readMagicResistance()));
        objReturn.putFloat(Params.ModuleChracter.AGILITY, Math.round(stats.readAgility()));
        objReturn.putFloat(Params.ModuleChracter.CRIT, (float) Utils.round(stats.readCrit(), 3));
        objReturn.putFloat(Params.ModuleChracter.CRIT_DAMAGE, (float) Utils.round(stats.readCritDmg(), 3));
        objReturn.putFloat(Params.ModuleChracter.ARMOR_PENETRATION, (float) Utils.round(stats.readArmorPenetration(), 3));
        objReturn.putFloat(Params.ModuleChracter.MAGIC_PENETRATION, (float) Utils.round(stats.readMagicPenetration(), 3));
        objReturn.putFloat(Params.ModuleChracter.TENACITY, Math.round(stats.readTenacity()));
        objReturn.putFloat(Params.ModuleChracter.ELUSIVENESS, Math.round(stats.readElusiveness()));

        return objReturn;
    }
    public static ISFSObject statsHeroBlessingToSFSObject(Stats stats){
        ISFSObject objReturn = new SFSObject();
        objReturn.putInt(Params.POWER, HeroManager.getInstance().getPower(stats));
        objReturn.putFloat(Params.ModuleChracter.HP, Math.round(stats.readHp()));
        objReturn.putFloat(Params.ModuleChracter.STRENGTH, Math.round(stats.readStrength()));
        objReturn.putFloat(Params.ModuleChracter.INTELLIGENCE, Math.round(stats.readIntelligence()));
        objReturn.putFloat(Params.ModuleChracter.ARMOR, Math.round(stats.readArmor()));
        objReturn.putFloat(Params.ModuleChracter.MAGIC_RESISTANCE, Math.round(stats.readMagicResistance()));
        objReturn.putFloat(Params.ModuleChracter.AGILITY, Math.round(stats.readAgility()));

        return objReturn;
    }

    /**
     * Statsb hero To SFSArray
     */
    public static ISFSArray statsHeroToSFSAray(Stats stats){
        ISFSArray objReturn = new SFSArray();
        ISFSObject objStats;
        float count;

        if((count = Math.round(stats.readHp())) != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.HP.getValue());
            objStats.putFloat(Params.PARAM, count);
            objReturn.addSFSObject(objStats);
        }
        if((count = Math.round(stats.readStrength())) != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.STRENGTH.getValue());
            objStats.putFloat(Params.PARAM, count);
            objReturn.addSFSObject(objStats);
        }
        if((count = Math.round(stats.readIntelligence())) != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.INTELLIGENCE.getValue());
            objStats.putFloat(Params.PARAM, count);
            objReturn.addSFSObject(objStats);
        }
        if((count = Math.round(stats.readDexterity())) != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.DEXTERITY.getValue());
            objStats.putFloat(Params.PARAM, count);
            objReturn.addSFSObject(objStats);
        }
        if((count = Math.round(stats.readArmor())) != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.ARMOR.getValue());
            objStats.putFloat(Params.PARAM, count);
            objReturn.addSFSObject(objStats);
        }
        if((count = Math.round(stats.readMagicResistance())) != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.MAGIC_RESISTANCE.getValue());
            objStats.putFloat(Params.PARAM, count);
            objReturn.addSFSObject(objStats);
        }
        if((count = Math.round(stats.readAgility())) != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.AGILITY.getValue());
            objStats.putFloat(Params.PARAM, count);
            objReturn.addSFSObject(objStats);
        }
        if((count = (float) Utils.round(stats.readCrit(), 3)) != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.CRITICAL_CHANCE.getValue());
            objStats.putFloat(Params.PARAM, count);
            objReturn.addSFSObject(objStats);
        }
        if((count = (float) Utils.round(stats.readCritDmg(), 3)) != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.CRITICAL_BONUS_DAMAGE.getValue());
            objStats.putFloat(Params.PARAM, count);
            objReturn.addSFSObject(objStats);
        }
        if((count = (float) Utils.round(stats.readArmorPenetration(), 3)) != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.ARMOR_PENETRATION.getValue());
            objStats.putFloat(Params.PARAM, count);
            objReturn.addSFSObject(objStats);
        }
        if((count = (float) Utils.round(stats.readMagicPenetration(), 3)) != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.MAGIC_PENETRATION.getValue());
            objStats.putFloat(Params.PARAM, count);
            objReturn.addSFSObject(objStats);
        }
        if((count = Math.round(stats.readTenacity())) != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.TENACITY.getValue());
            objStats.putFloat(Params.PARAM, count);
            objReturn.addSFSObject(objStats);
        }
        if((count = Math.round(stats.readElusiveness())) != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.ELUSIVENESS.getValue());
            objStats.putFloat(Params.PARAM, count);
            objReturn.addSFSObject(objStats);
        }

        return objReturn;
    }


    /**
     * Stats To SFSObject
     */
    public static ISFSObject statsCelestialToSFSObject(Stats stats){
        ISFSObject objReturn = new SFSObject();
        objReturn.putFloat(Params.ModuleChracter.ATTACK, Math.round(stats.readStrength()));
        objReturn.putFloat(Params.ModuleChracter.CRIT, (float) Utils.round(stats.readCrit(), 3));
        objReturn.putFloat(Params.ModuleChracter.CRIT_DAMAGE, (float) Utils.round(stats.readCritDmg(), 3));
        objReturn.putFloat(Params.ModuleChracter.DEFENCE_PENETRATION, (float) Utils.round(stats.readArmorPenetration(), 3));

        return objReturn;
    }

    /**
     * Stats To SFSArray
     */
    public static ISFSArray statsCelestialToSFSAray(Stats stats){
        ISFSArray objReturn = new SFSArray();
        ISFSObject objStats;
        if(stats.readStrength() != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.ATTACK.getValue());
            objStats.putFloat(Params.PARAM, Math.round(stats.readStrength()));
            objReturn.addSFSObject(objStats);
        }
        if(stats.readCrit() != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.CRITICAL_CHANCE.getValue());
            objStats.putFloat(Params.PARAM, (float) Utils.round(stats.readCrit(), 3));
            objReturn.addSFSObject(objStats);
        }
        if(stats.readCritDmg() != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.CRITICAL_BONUS_DAMAGE.getValue());
            objStats.putFloat(Params.PARAM, (float) Utils.round(stats.readCritDmg(), 3));
            objReturn.addSFSObject(objStats);
        }
        if(stats.readArmorPenetration() != 0){
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.ARMOR_PENETRATION.getValue());
            objStats.putFloat(Params.PARAM, (float) Utils.round(stats.readArmorPenetration(), 3));
            objReturn.addSFSObject(objStats);
        }

        return objReturn;
    }



    /**
     * Stats To SFSObject
     */
    public static ISFSObject statsMageToSFSObject(Stats stats){
        ISFSObject objReturn = new SFSObject();
        objReturn.putFloat(Params.ModuleChracter.ATTACK, Math.round(stats.readAttack()));
        objReturn.putFloat(Params.ModuleChracter.CRIT, Math.round(stats.readCrit() * 1000) / 1000.f);
        objReturn.putFloat(Params.ModuleChracter.CRIT_DAMAGE, Math.round(stats.readCritDmg() * 1000) / 1000.f);
        objReturn.putFloat(Params.ModuleChracter.DEFENCE_PENETRATION, Math.round(stats.readDefensePenetration() * 1000) / 1000.f);

        return objReturn;
    }

    /**
     * Stats To SFSArray
     */
    public static ISFSArray statsMageToSFSAray(Stats stats){
        ISFSArray objReturn = new SFSArray();
        ISFSObject objStats;
        if (stats.readAttack() != 0) {
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.ATTACK.getValue());
            objStats.putFloat(Params.PARAM, Math.round(stats.readStrength()));
            objReturn.addSFSObject(objStats);
        }
        if (stats.readCrit() != 0) {
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.CRITICAL_BONUS_DAMAGE.getValue());
            objStats.putFloat(Params.PARAM, Math.round(stats.readCrit() * 1000) / 1000f);
            objReturn.addSFSObject(objStats);
        }
        if (stats.readCritDmg() != 0) {
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.CRITICAL_CHANCE.getValue());
            objStats.putFloat(Params.PARAM, Math.round(stats.readCritDmg() * 1000) / 1000f);
            objReturn.addSFSObject(objStats);
        }
        if (stats.readDefensePenetration() != 0) {
            objStats = new SFSObject();
            objStats.putInt(Params.ID, Attr.DEFENSE_PENETRATION.getValue());
            objStats.putFloat(Params.PARAM, Math.round(stats.readArmorPenetration() * 1000) / 1000f);
            objReturn.addSFSObject(objStats);
        }

        return objReturn;
    }
}
