package com.bamisu.log.gameserver.module.darkgate.entities;

/**
 * Create by Popeye on 4:14 PM, 11/13/2020
 */
public enum EventStatus {
    ACTIVE(0),
    INACTIVE(1),
    WAITING(2);

    public int intValue;

    EventStatus(int intValue) {
        this.intValue = intValue;
    }

    public static EventStatus fromIntValue(int intValue){
        for(EventStatus eventStatus : values()){
            if(eventStatus.intValue == intValue){
                return eventStatus;
            }
        }
        return null;
    }
}
