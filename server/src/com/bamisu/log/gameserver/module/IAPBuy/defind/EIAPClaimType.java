package com.bamisu.log.gameserver.module.IAPBuy.defind;

public enum  EIAPClaimType {
    FREE("0"),
    PREDIUM("1"),
    FREE_PREDIUM("2"),
    ALL("3"),
    ACTIVE_PREDIUM("4");

    String id;

    public String getId() {
        return id;
    }

    EIAPClaimType(String id) {
        this.id = id;
    }

    public static EIAPClaimType fromID(String id){
        for(EIAPClaimType type : EIAPClaimType.values()){
            if(type.id.equals(id)){
                return type;
            }
        }
        return FREE;
    }
}
