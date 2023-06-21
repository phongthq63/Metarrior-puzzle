package com.bamisu.log.gameserver.module.characters.entities;

import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.creep.entities.CreepInstanceVO;
import com.bamisu.log.gameserver.module.characters.creep.entities.CreepVO;
import com.bamisu.log.gameserver.module.characters.kingdom.Kingdom;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;

import java.util.ArrayList;
import java.util.List;

public class Creep implements ICharacter {
    public int characterType = ECharacterType.Creep.getType();
    public CreepInstanceVO creepInstance;
    public Stats stats;
    public int power;
    public List<SkillInfo> skillInfos = new ArrayList<>();
    public int lethal = 0;

    public static Creep createCreep(String id, int level, int star, String kingdom, String element, int lethal){
        Creep creep = new Creep();
        creep.creepInstance = CreepInstanceVO.createCreepInstanceVO(id, level, star, kingdom, element);
        creep.stats = HeroManager.CreepManager.getInstance().getStatsCreep(creep.creepInstance);
        creep.power = HeroManager.getInstance().getPower(creep.stats);
        creep.lethal = lethal;

        //skill
        CreepVO creepVO = CharactersConfigManager.getInstance().getCreepConfig(creep.creepInstance.id);
        for(String skillID : creepVO.skills){
            creep.skillInfos.add(new SkillInfo(skillID, 1));
        }

        return creep;
    }

    public Stats readStats() {
        if(stats == null){
            stats = readBasicStatsCreep();
        }
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    /**
     * Luc chien Creep
     * @return
     */
    public int readPowerCreep(){
        return power;
    }

    /**
     * Stats Creep (cap + sao -> config)
     * @return
     */
    public Stats readBasicStatsCreep(){
        return stats;
    }

    @Override
    public String readID() {
        return creepInstance.id;
    }

    @Override
    public Object getSkill() {
        return skillInfos;
    }

    @Override
    public int readLevel() {
        return creepInstance.level;
    }

    @Override
    public int readStar() {
        return creepInstance.star;
    }

    @Override
    public String readElement() {
        return creepInstance.element;
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
        return 0;
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
        return 0;
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
        return 0;
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
