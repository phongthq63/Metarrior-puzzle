package com.bamisu.log.gameserver.module.darkgate.entities;

/**
 * Create by Popeye on 10:14 AM, 11/12/2020
 */
public enum EDarkGateEvent {
    Dark_Realm(0, "Dark Realm"),
    Endless_Nights(1, "Endless Nights");

    public int id;
    public String name;

    EDarkGateEvent(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static EDarkGateEvent fromID(int id){
        for (EDarkGateEvent eDarkGateEvent : values()){
            if(eDarkGateEvent.id == id){
                return eDarkGateEvent;
            }
        }

        return null;
    }
}
