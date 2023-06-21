package com.bamisu.log.gameserver.module.ingame.entities.skill;

import com.bamisu.log.gameserver.module.skill.DamageType;

/**
 * Create by Popeye on 3:48 PM, 3/3/2020
 */
public class Damage {
    int value;
    DamageType type;

    public Damage(int value) {
        this.value = value;
    }

    public Damage(int value, DamageType type) {
        this.value = value;
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public DamageType getType() {
        return type;
    }

    public void setType(DamageType type) {
        this.type = type;
    }

    public Damage cloneNew() {
        return new Damage(this.value, this.type);
    }

    public Damage addDamage(int value){
        this.value += value;
        return this;
    }
}
