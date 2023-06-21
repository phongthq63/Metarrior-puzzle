package com.bamisu.log.gameserver.module.characters.hero.entities;

import com.bamisu.log.gameserver.module.characters.entities.ICharacterVO;

import java.util.ArrayList;
import java.util.List;

public class HeroVO implements ICharacterVO {
    public int status;
    public String id;
    public String name;
    public String description;
    public String gender;
    public String kingdom;
    public String clas;
    public String element;
    public String role;
    public byte star;
    public byte maxStar;
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
    public List<String> skill = new ArrayList<>();
    public short bonusDiamond;
    public byte rare;
    public byte piece;
    public String weaponType;
    public Byte breed;

    public static HeroVO createHero(HeroVO heroCf){
        HeroVO heroVO = new HeroVO();
        heroVO.id = heroCf.id;
        heroVO.name = heroCf.name;
        heroVO.description = heroCf.description;
        heroVO.gender = heroCf.gender;
        heroVO.kingdom = heroCf.kingdom;
        heroVO.clas = heroCf.clas;
        heroVO.element = heroCf.element;
        heroVO.role = heroCf.role;
        heroVO.star = heroCf.star;
        heroVO.maxStar = heroCf.maxStar;
        heroVO.health = heroCf.health;
        heroVO.strength = heroCf.strength;
        heroVO.intelligence = heroCf.intelligence;
        heroVO.dexterity = heroCf.dexterity;
        heroVO.armor = heroCf.armor;
        heroVO.magicResistance = heroCf.magicResistance;
        heroVO.agility = heroCf.agility;
        heroVO.crit = heroCf.crit;
        heroVO.critBonus = heroCf.critBonus;
        heroVO.armorPenetration = heroCf.armorPenetration;
        heroVO.magicPenetration = heroCf.magicPenetration;
        heroVO.tenacity = heroCf.tenacity;
        heroVO.elusiveness = heroCf.elusiveness;
        heroVO.skill = heroCf.skill;
        heroVO.bonusDiamond = heroCf.bonusDiamond;
        heroVO.rare = heroCf.rare;
        heroVO.piece = heroCf.piece;
        heroVO.weaponType = heroCf.weaponType;
        if (heroCf.breed == null) {
            heroCf.breed = 10; // default, chua co config (remove sau)
        }

        heroVO.breed = heroCf.breed;

        return heroVO;
    }

    public static HeroVO createHero(String id, String name, String description, String gender,
                                    String kingdom, String clas, String element, String role,
                                    int star,
                                    int hp, int physicalDmg, int magicDmg, int acc, int physicalResist,
                                    int magicResist, int speed, int crit, int critDmg, int armorPenetration,
                                    int magicPenetration, int resistance, int dodge,
                                    List<String> skill,
                                    int bonusDiamond,
                                    int rare,
                                    int piece,
                                    String weaponType) {
        HeroVO heroVO = new HeroVO();
        heroVO.id = id;
        heroVO.name = name;
        heroVO.description = description;
        heroVO.gender = gender;
        heroVO.kingdom = kingdom;
        heroVO.clas = clas;
        heroVO.element = element;
        heroVO.role = role;
        heroVO.star = (byte) star;
        heroVO.health = hp;
        heroVO.strength = physicalDmg;
        heroVO.intelligence = magicDmg;
        heroVO.dexterity = acc;
        heroVO.armor = physicalResist;
        heroVO.magicResistance = magicResist;
        heroVO.agility = speed;
        heroVO.crit = crit;
        heroVO.critBonus = critDmg;
        heroVO.armorPenetration = armorPenetration;
        heroVO.magicPenetration = magicPenetration;
        heroVO.tenacity = resistance;
        heroVO.elusiveness = dodge;
        heroVO.skill = skill;
        heroVO.bonusDiamond = (short) bonusDiamond;
        heroVO.rare = (byte) rare;
        heroVO.piece = (byte) piece;
        heroVO.weaponType = weaponType;

        return heroVO;
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
        return kingdom;
    }

    @Override
    public String readelement() {
        return element;
    }
}
