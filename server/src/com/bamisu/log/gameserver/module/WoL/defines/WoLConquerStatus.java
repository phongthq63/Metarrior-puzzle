package com.bamisu.log.gameserver.module.WoL.defines;

public enum WoLConquerStatus {
    INCOMPLETE(1),
    ALREADY_RECEIVED(2),
    CAN_RECEIVE(3);

    public int status;

    WoLConquerStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public static WoLConquerStatus fromStatus(int status){
        for(WoLConquerStatus index : WoLConquerStatus.values()){
            if(index.status == status){
                return index;
            }
        }
        return null;
    }
}
