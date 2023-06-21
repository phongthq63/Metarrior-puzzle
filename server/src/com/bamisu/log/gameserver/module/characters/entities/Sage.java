package com.bamisu.log.gameserver.module.characters.entities;

import com.bamisu.log.gameserver.datamodel.mage.SageSkillModel;
import com.bamisu.log.gameserver.datamodel.mage.UserMageModel;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.bamisu.log.gameserver.module.mage.MageManager;
import com.smartfoxserver.v2.entities.Zone;

public class Sage implements ICharacter {
    public int characterType = ECharacterType.Sage.getType();
    public UserMageModel mageModel;
    public Stats stats;
    public int power;
    public SageSkillModel sageSkillModel;

    public static Sage createMage(Zone zone, long uid){
        Sage gage = new Sage();
        gage.mageModel = MageManager.getInstance().getUserMageModel(zone, uid);
        gage.stats = MageManager.getInstance().getStatsMage(gage.mageModel, zone);
        gage.power = MageManager.getInstance().getPower(gage.stats);
        gage.sageSkillModel = MageManager.getInstance().getSageSkillModel(zone, uid);

        return gage;
    }

    /**
     * Trang phuc cua phap su
     * @return
     */
    public String readSkinSage(){
        return mageModel.readSkin();
    }

    /**
     * Luc chien Hero
     * @param stats
     * @return
     */
    public int readPowerMage(Stats stats, MageManager manager){
        return power;
    }

    /**
     * Stats Hero mac do
     * @param mageModel
     * @return
     */
    public Stats readStatsMage(UserMageModel mageModel, Zone zone){
        return stats;
    }

    public Stats readStats() {
        return (stats == null) ? new Stats() : stats;
    }

    @Override
    public String readID() {
        return String.valueOf(mageModel.uid);
    }

    @Override
    public Object getSkill() {
        return sageSkillModel;
    }

    @Override
    public int readLevel() {
        return mageModel.level;
    }

    @Override
    public int readStar() {
        return 0;
    }

    @Override
    public String readElement() {
        return "";
    }

    @Override
    public String readKingdom() {
        return "";
    }

    @Override
    public int readCharacterType() {
        return characterType;
    }

    @Override
    public float readHP() {
        return 0;
    }

    @Override
    public float readSTR() {
        return 0;
    }

    @Override
    public float readINT() {
        return 0;
    }

    @Override
    public float readATK() {
        return readStats().readAttack();
    }

    @Override
    public float readDEX() {
        return 0;
    }

    @Override
    public float readARM() {
        return 0;
    }

    @Override
    public float readMR() {
        return 0;
    }

    @Override
    public float readDEF() {
        return 0;
    }

    @Override
    public float readAGI() {
        return 0;
    }

    @Override
    public float readCRIT() {
        return readStats().readCrit();
    }

    @Override
    public float readCRITBONUS() {
        return readStats().readCritDmg();
    }

    @Override
    public float readAPEN() {
        return readStats().readDefensePenetration();
    }

    @Override
    public float readMPEN() {
        return readStats().readDefensePenetration();
    }

    @Override
    public float readDPEN() {
        return readStats().readDefensePenetration();
    }

    @Override
    public float readTEN() {
        return 0;
    }

    @Override
    public float readELU() {
        return 0;
    }

    @Override
    public int getLethal() {
        return 0;
    }
}
