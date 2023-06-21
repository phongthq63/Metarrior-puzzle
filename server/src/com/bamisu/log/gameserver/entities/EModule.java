package com.bamisu.log.gameserver.entities;

public enum  EModule {
    IAP("iap"),
    QUEST("quest");

    String id;

    public String getId() {
        return id;
    }

    EModule(String id) {
        this.id = id;
    }

    public static EModule fromID(String id){
        for(EModule module : EModule.values()){
            if(module.id.equals(id)){
                return module;
            }
        }
        return null;
    }
}
