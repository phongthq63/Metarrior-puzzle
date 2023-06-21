package com.bamisu.log.gameserver.module.IAPBuy.defind;

public enum EIAPType {
    PACKAGE_ITEM(0),
    CHALLENGE(1);

    int id;

    EIAPType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static EIAPType fromID(int id){
        for(EIAPType index : EIAPType.values()){
            if(index.id == id){
                return index;
            }
        }
        return null;
    }
}
