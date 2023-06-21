package com.bamisu.log.gameserver.module.darkgate.entities;

/**
 * Create by Popeye on 8:30 AM, 11/26/2020
 */
public enum EDarkGateState {
    IN_WEEK(0),
    END_WEEK(1);

    int intValue;

    EDarkGateState(int i) {
        intValue = i;
    }
}
