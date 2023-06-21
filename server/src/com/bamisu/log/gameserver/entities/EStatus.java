package com.bamisu.log.gameserver.entities;

public enum EStatus {
    ENABLE(0),
    DISABLE(1),
    COMMING_SOON(2);

    int id;

    EStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static EStatus fromId(int id){
        for(EStatus status : EStatus.values()){
            if(status.id == id){
                return status;
            }
        }
        return null;
    }
}
