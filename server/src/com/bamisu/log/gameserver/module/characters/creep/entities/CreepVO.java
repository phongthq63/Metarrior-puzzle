package com.bamisu.log.gameserver.module.characters.creep.entities;

import com.bamisu.log.gameserver.module.characters.entities.ICharacterVO;
import com.bamisu.log.gameserver.module.characters.kingdom.Kingdom;

import java.util.List;

public class CreepVO implements ICharacterVO {
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

    public static CreepVO createCreep(CreepVO creepCf){
        CreepVO creepVO = new CreepVO();
        creepVO.id = creepCf.id;
        creepVO.name = creepCf.name;
        creepVO.element = creepCf.element;
        creepVO.tag = creepCf.tag;
        creepVO.health = creepCf.health;
        creepVO.strength = creepCf.strength;
        creepVO.intelligence = creepCf.intelligence;
        creepVO.dexterity = creepCf.dexterity;
        creepVO.armor = creepCf.armor;
        creepVO.magicResistance = creepCf.magicResistance;
        creepVO.agility = creepCf.agility;
        creepVO.crit = creepCf.crit;
        creepVO.critBonus = creepCf.critBonus;
        creepVO.armorPenetration = creepCf.armorPenetration;
        creepVO.magicPenetration = creepCf.magicPenetration;
        creepVO.tenacity = creepCf.tenacity;
        creepVO.elusiveness = creepCf.elusiveness;
        creepVO.skills = creepCf.skills;

        return creepVO;
    }

    public static CreepVO createCreep(String id, String name,
                                      int star,
                                      int hp, int physicalDmg, int magicDmg, int acc,
                                      int physicalResist, int magicResist, int speed, int crit, int critd,
                                      int armorPene, int magicPene, int resist, int dodge,
                                      List<String> skills) {
        CreepVO creepVO = new CreepVO();
        creepVO.id = id;
        creepVO.name = name;
        creepVO.star = (byte) star;
        creepVO.health = hp;
        creepVO.strength = physicalDmg;
        creepVO.intelligence = magicDmg;
        creepVO.dexterity = acc;
        creepVO.armor = physicalResist;
        creepVO.magicResistance = magicResist;
        creepVO.agility = speed;
        creepVO.crit = crit;
        creepVO.critBonus = critd;
        creepVO.armorPenetration = armorPene;
        creepVO.magicPenetration = magicPene;
        creepVO.tenacity = resist;
        creepVO.elusiveness = dodge;
        creepVO.skills = skills;
        
        return creepVO;
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
