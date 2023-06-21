package com.bamisu.log.gameserver.module.IAPBuy.defind;

public enum  EIAPIncreaseType {
    POINT("point"),
    CHAP("chap"),
    NEW_DAY("time"),
    CHECKIN_NEW_DAY("checkin");

    EIAPIncreaseType(String id) {
        this.id = id;
    }

    String id;

    public static EIAPIncreaseType fromId(String id){
        for(EIAPIncreaseType type : EIAPIncreaseType.values()){
            if(type.id.equals(id)){
                return type;
            }
        }
        return EIAPIncreaseType.POINT;
    }
}
