package com.bamisu.log.gameserver.module.ingame.entities.skill;

/**
 * Create by Popeye on 10:20 AM, 2/21/2020
 */
public enum ComboType {
    NONE(0, "none"),
    HEALS(1, "heals"),
    SKILL2(2, "skill2"),
    SKILL3(3, "skill3");

    int value;
    String name;

    ComboType(int value, String name){
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }


    @Override
    public String toString() {
        return name;
    }
}
