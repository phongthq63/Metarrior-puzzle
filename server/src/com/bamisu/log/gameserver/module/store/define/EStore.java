package com.bamisu.log.gameserver.module.store.define;

public enum EStore {
    GENERAL_STORE(0),
    ALLIANCE_STORE(1),
    MEMOIR_STORE(2),
    HUNTER_STORE(3);
    int id;

    EStore(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static EStore fromID(int id){
        for(EStore index : EStore.values()){
            if(index.id == id){
                return index;
            }
        }
        return null;
    }
}
