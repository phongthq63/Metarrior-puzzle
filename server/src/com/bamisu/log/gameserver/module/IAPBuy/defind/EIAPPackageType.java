package com.bamisu.log.gameserver.module.IAPBuy.defind;

public enum EIAPPackageType {
    BUY_TIME_REFRESH("0"),
    BUY_TIME("1"),
    BUY_REFRESH("2"),
    TIME_REFRESH("3"),
    BUY("4"),
    TIME("5"),
    REFRESH("6"),
    EXTANT("7");

    String id;

    public String getId() {
        return id;
    }

    EIAPPackageType(String id) {
        this.id = id;
    }

    public static EIAPPackageType fromID(String id){
        for(EIAPPackageType type : EIAPPackageType.values()){
            if(type.id.equals(id)){
                return type;
            }
        }
        return null;
    }
}
