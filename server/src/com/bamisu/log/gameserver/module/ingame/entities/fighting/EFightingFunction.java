package com.bamisu.log.gameserver.module.ingame.entities.fighting;

/**
 * Create by Popeye on 3:57 PM, 5/12/2020
 */
public enum EFightingFunction {
    CAMPAIGN(0),
    MISSION(1),
    HUNT(2),
    TOWER(3),
    PvP_FRIEND(4),
    PvP_ARENA(5),
    DARK_REALM(6),
    ENDLESS_NIGHT(7);


    int intValue;

    EFightingFunction(int intValue) {
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }
}
