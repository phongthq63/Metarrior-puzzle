package com.bamisu.log.gameserver.module.characters.mboss.entities;

import com.bamisu.log.gameserver.module.characters.entities.ICharacterVO;
import com.bamisu.log.gameserver.module.characters.kingdom.Kingdom;

import java.util.List;

/**
 * Create by Popeye on 11:23 AM, 4/28/2020
 */
public class MbossVO implements ICharacterVO {
    public String id;
    public String name;
    public String element;
    public int tag = 0;
    public byte star;
    public float health;
    public float strength;
    public float intelligence;
    public float dexterity;
    public float armor;
    public float magicResistance;
    public float agility;
    public float crit;
    public float critBonus;
    public float armorPenetration;
    public float magicPenetration;
    public float tenacity;
    public float elusiveness;
    public List<String> skills;

    public static MbossVO createMboss(MbossVO mbossCf){
        MbossVO creepVO = new MbossVO();
        creepVO.id = mbossCf.id;
        creepVO.element = mbossCf.element;
        creepVO.tag = mbossCf.tag;
        creepVO.name = mbossCf.name;
        creepVO.health = mbossCf.health;
        creepVO.strength = mbossCf.strength;
        creepVO.intelligence = mbossCf.intelligence;
        creepVO.dexterity = mbossCf.dexterity;
        creepVO.armor = mbossCf.armor;
        creepVO.magicResistance = mbossCf.magicResistance;
        creepVO.agility = mbossCf.agility;
        creepVO.crit = mbossCf.crit;
        creepVO.critBonus = mbossCf.critBonus;
        creepVO.armorPenetration = mbossCf.armorPenetration;
        creepVO.magicPenetration = mbossCf.magicPenetration;
        creepVO.tenacity = mbossCf.tenacity;
        creepVO.elusiveness = mbossCf.elusiveness;
        creepVO.skills = mbossCf.skills;

        return creepVO;
    }

    public static MbossVO createMboss(String id, String name,
                                      int star,
                                      int hp, int physicalDmg, int magicDmg, int acc,
                                      int physicalResist, int magicResist, int speed, int crit, int critd,
                                      int armorPene, int magicPene, int resist, int dodge,
                                      List<String> skills) {
        MbossVO mBossVO = new MbossVO();
        mBossVO.id = id;
        mBossVO.name = name;
        mBossVO.star = (byte) star;
        mBossVO.health = hp;
        mBossVO.strength = physicalDmg;
        mBossVO.intelligence = magicDmg;
        mBossVO.dexterity = acc;
        mBossVO.armor = physicalResist;
        mBossVO.magicResistance = magicResist;
        mBossVO.agility = speed;
        mBossVO.crit = crit;
        mBossVO.critBonus = critd;
        mBossVO.armorPenetration = armorPene;
        mBossVO.magicPenetration = magicPene;
        mBossVO.tenacity = resist;
        mBossVO.elusiveness = dodge;
        mBossVO.skills = skills;

        return mBossVO;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public int readStar() {
        return star;
    }

    @Override
    public String readkingdom() {
        return Kingdom.DARK.getId();
    }

    @Override
    public String readelement() {
        return null;
    }
}
