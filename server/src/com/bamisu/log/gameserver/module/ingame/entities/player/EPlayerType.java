package com.bamisu.log.gameserver.module.ingame.entities.player;

/**
 * Create by Popeye on 10:13 AM, 5/15/2020
 */
public enum EPlayerType {
    HUMAN(0),
    NPC(1);

    private int intValue;

    public int getIntValue() {
        return intValue;
    }

    EPlayerType(int intValue) {
        this.intValue = intValue;
    }
}
