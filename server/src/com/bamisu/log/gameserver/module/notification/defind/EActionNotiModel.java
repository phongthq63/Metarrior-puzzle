package com.bamisu.log.gameserver.module.notification.defind;

public enum EActionNotiModel {
    SHOW("0"),
    REMOVE("1"),
    REFRESH("2");

    String id;

    public String getId() {
        return id;
    }

    EActionNotiModel(String id) {
        this.id  = id;
    }

    public static EActionNotiModel fromID(String id){
        for(EActionNotiModel index : EActionNotiModel.values()){
            if(index.id.equals(id)){
                return index;
            }
        }
        return null;
    }
}
