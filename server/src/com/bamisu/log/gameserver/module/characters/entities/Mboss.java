package com.bamisu.log.gameserver.module.characters.entities;

import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.kingdom.Kingdom;
import com.bamisu.log.gameserver.module.characters.mboss.entities.MbossInstanceVO;
import com.bamisu.log.gameserver.module.characters.mboss.entities.MbossVO;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;

import java.util.ArrayList;
import java.util.List;

public class Mboss implements ICharacter {
    public int characterType = ECharacterType.MiniBoss.getType();
    public MbossInstanceVO mBossInstance;
    public Stats stats;
    public int power;
    public List<SkillInfo> skillInfos = new ArrayList<>();
    public int lethal = 0;

    public static Mboss createMBoss(String id, int level, int star, String kingdom, String element, int lethal){
        Mboss mBoss = new Mboss();
        mBoss.mBossInstance = MbossInstanceVO.createMBossInstanceVO(id, level, star, kingdom, element);
        mBoss.stats = HeroManager.MBossManager.getInstance().getStatsMBoss(mBoss.mBossInstance);
        mBoss.power = HeroManager.getInstance().getPower(mBoss.stats);
        mBoss.lethal = lethal;

        //skill
        MbossVO mbossVO = CharactersConfigManager.getInstance().getMbossConfig(mBoss.mBossInstance.id);
        for(String skillID : mbossVO.skills){
            mBoss.skillInfos.add(new SkillInfo(skillID, 1));
        }

        return mBoss;
    }

    public Stats readStats() {
        if(stats == null){
            stats = readBasicStatsMboss(mBossInstance);
        }
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    /**
     * Luc chien MBoss
     * @param stats
     * @return
     */
    public int readPowerMboss(Stats stats){
        return HeroManager.getInstance().getPower(stats);
    }

    /**
     * Stats Mboss (cap + sao -> config)
     * @param mBossInstance
     * @return
     */
    public Stats readBasicStatsMboss(MbossInstanceVO mBossInstance){
        return HeroManager.MBossManager.getInstance().getStatsMBoss(mBossInstance);
    }

    @Override
    public String readID() {
        return mBossInstance.id;
    }

    @Override
    public Object getSkill() {
        return skillInfos;
    }

    @Override
    public int readLevel() {
        return mBossInstance.level;
    }

    @Override
    public int readStar() {
        return mBossInstance.star;
    }

    @Override
    public String readElement() {
        return mBossInstance.element;
    }

    @Override
    public String readKingdom() {
        return Kingdom.DARK.getId();
    }

    @Override
    public int readCharacterType() {
        return characterType;
    }

    /**
     * Mau
     * @return
     */
    @Override
    public float readHP() {
        return readStats().readHp() * (100 + getLethal()) / 100;
    }

    /**
     * Damage vat ly
     * @return
     */
    @Override
    public float readSTR() {
        return readStats().readStrength() * (100 + getLethal()) / 100;
    }

    /**
     * Damage phep
     * @return
     */
    @Override
    public float readINT() {
        return readStats().readIntelligence() * (100 + getLethal()) / 100;
    }

    @Override
    public float readATK() {
        return readStats().readAttack();
    }

    /**
     * Chinh xac
     * @return
     */
    @Override
    public float readDEX() {
        return readStats().readDexterity();
    }

    /**
     * Giap
     * @return
     */
    @Override
    public float readARM() {
        return readStats().readArmor();
    }

    /**
     * Khang phep
     * @return
     */
    @Override
    public float readMR() {
        return readStats().readMagicResistance();
    }

    @Override
    public float readDEF() {
        return readStats().readDefense();
    }

    /**
     * Toc do
     * @return
     */
    @Override
    public float readAGI() {
        return readStats().readAgility();
    }

    /**
     * Ti le gay chi mang
     * @return
     */
    @Override
    public float readCRIT() {
        return readStats().readCrit();
    }

    /**
     * Ti le dame khi chi mang
     * @return
     */
    @Override
    public float readCRITBONUS() {
        return readStats().readCritDmg();
    }

    /**
     * Ti le xuyen giap
     * @return
     */
    @Override
    public float readAPEN() {
        return readStats().readArmorPenetration();
    }

    /**
     * Ti le xuyen khang phep
     * @return
     */
    @Override
    public float readMPEN() {
        return readStats().readMagicPenetration();
    }

    @Override
    public float readDPEN() {
        return readStats().readDefensePenetration();
    }

    /**
     * Ti le khang hieu ung
     * @return
     */
    @Override
    public float readTEN() {
        return readStats().readTenacity();
    }

    /**
     * Ti le ne
     * @return
     */
    @Override
    public float readELU() {
        return readStats().readElusiveness();
    }

    @Override
    public int getLethal() {
        return lethal;
    }
}
