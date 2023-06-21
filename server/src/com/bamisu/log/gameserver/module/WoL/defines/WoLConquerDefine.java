package com.bamisu.log.gameserver.module.WoL.defines;

public enum WoLConquerDefine {
    CAN(true),
    CAN_NOT(false);

    boolean status;
    WoLConquerDefine(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public static WoLConquerDefine fromStatus(boolean status){
        for(WoLConquerDefine index : WoLConquerDefine.values()){
            if(index.status == status){
                return index;
            }
        }
        return null;
    }
}
