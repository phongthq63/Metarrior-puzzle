package com.bamisu.log.gameserver.module.hero.entities;

import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.characters.celestial.entities.CelestialVO;
import com.bamisu.log.gameserver.module.characters.mage.MageConfig;

public class Stats {
    public float hp;
    public float strength;
    public float intelligence;
    public float attack;
    public float armor;
    public float magicResistance;
    public float defense;
    public float dexterity;
    public float agility;
    public float elusiveness;
    public float armorPenetration;
    public float magicPenetration;
    public float defensePenetration;
    public float crit;
    public float critDmg;
    public float tenacity;
    

    public Stats() {
    }

    public Stats(Stats stats) {
        this.hp = stats.hp;
        this.strength = stats.strength;
        this.intelligence = stats.intelligence;
        this.attack = stats.attack;
        this.armor = stats.armor;
        this.magicResistance = stats.magicResistance;
        this.defense = stats.defense;
        this.dexterity = stats.dexterity;
        this.agility = stats.agility;
        this.elusiveness = stats.elusiveness;
        this.armorPenetration = stats.armorPenetration;
        this.magicPenetration = stats.magicPenetration;
        this.defensePenetration = stats.defensePenetration;
        this.crit = stats.crit;
        this.critDmg = stats.critDmg;
        this.tenacity = stats.tenacity;
    }

    public Stats(MageConfig statsBasic) {
        this.attack = statsBasic.atk;
        this.crit = statsBasic.crit;
        this.critDmg = statsBasic.critBonus;
        this.defensePenetration = statsBasic.defensePenetration;
    }

    public Stats(CelestialVO celestialCf) {
        this.attack = celestialCf.atk;
        this.crit = celestialCf.crit;
        this.critDmg = celestialCf.critDmg;
        this.defensePenetration = celestialCf.pierce;
    }

    /**
     * Tong chi so
     */
    public static Stats readSumStats(Stats stats1, Stats stats2){
        Stats stats = new Stats();
        stats.hp = stats1.hp + stats2.hp;
        stats.strength = stats1.strength + stats2.strength;
        stats.attack = stats1.attack + stats2.attack;
        stats.intelligence = stats1.intelligence + stats2.intelligence;
        stats.dexterity = stats1.dexterity + stats2.dexterity;
        stats.armor = stats1.armor + stats2.armor;
        stats.magicResistance = stats1.magicResistance + stats2.magicResistance;
        stats.defense = stats1.defense + stats2.defense;
        stats.agility = stats1.agility + stats2.agility;
        stats.crit = stats1.crit + stats2.crit;
        stats.critDmg = stats1.critDmg + stats2.critDmg;
        stats.armorPenetration = stats1.armorPenetration + stats2.armorPenetration;
        stats.magicPenetration = stats1.magicPenetration + stats2.magicPenetration;
        stats.defensePenetration = stats1.defensePenetration + stats2.defensePenetration;
        stats.tenacity = stats1.tenacity + stats2.tenacity;
        stats.elusiveness = stats1.elusiveness + stats2.elusiveness;

        return stats;
    }

    /**
     * Chech lech chi so
     */
    public static Stats readDisparitiesStats(Stats before, Stats after){
        Stats stats = new Stats();
        stats.hp = after.hp - before.hp;
        stats.strength = after.strength - before.strength;
        stats.intelligence = after.intelligence - before.intelligence;
        stats.attack = after.attack - before.attack;
        stats.dexterity = after.dexterity - before.dexterity;
        stats.armor = after.armor - before.armor;
        stats.magicResistance = after.magicResistance - before.magicResistance;
        stats.defense = after.defense - before.defense;
        stats.agility = after.agility - before.agility;
        stats.crit = after.crit - before.crit;
        stats.critDmg = after.critDmg - before.critDmg;
        stats.armorPenetration = after.armorPenetration - before.armorPenetration;
        stats.magicPenetration = after.magicPenetration - before.magicPenetration;
        stats.defensePenetration = after.defensePenetration - before.defensePenetration;
        stats.tenacity = after.tenacity - before.tenacity;
        stats.elusiveness = after.elusiveness - before.elusiveness;

        return stats;
    }

    public float readHp() {
        return hp;
    }

    public float readStrength() {
        return strength + attack;
    }

    public float readIntelligence() {
        return intelligence + attack;
    }

    public float readAttack() {
        return attack;
    }

    public float readArmor() {
        return armor + defense;
    }

    public float readMagicResistance() {
        return magicResistance + defense;
    }

    public float readDefense() {
        return defense;
    }

    public float readDexterity() {
        return dexterity;
    }

    public float readAgility() {
        return agility;
    }

    public float readElusiveness() {
        return elusiveness;
    }

    public float readArmorPenetration() {
        return armorPenetration + defensePenetration;
    }

    public float readMagicPenetration() {
        return magicPenetration + defensePenetration;
    }

    public float readDefensePenetration() {
        return defensePenetration;
    }

    public float readCrit() {
        return crit;
    }

    public float readCritDmg() {
        return critDmg;
    }

    public float readTenacity() {
        return tenacity;
    }

    public Stats cloneWithLethal(int lethal){
        Stats newStats = Utils.fromJson(Utils.toJson(this), Stats.class);

        newStats.hp += (newStats.hp * lethal) / 100;

        newStats.strength += (newStats.strength * lethal) / 100;
        newStats.intelligence += (newStats.intelligence * lethal) / 100;

        newStats.armor += (newStats.armor * lethal) / 100;
        newStats.magicResistance += (newStats.magicResistance * lethal) / 100;

        newStats.armorPenetration += (newStats.armorPenetration * lethal) / 100;
        newStats.magicPenetration += (newStats.magicPenetration * lethal) / 100;
        return newStats;
    }
}
