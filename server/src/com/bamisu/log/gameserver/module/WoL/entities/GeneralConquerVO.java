package com.bamisu.log.gameserver.module.WoL.entities;

public class GeneralConquerVO {
    public boolean status; //True: achieved - False: don't have
    public long uid;

    public GeneralConquerVO(boolean status, long uid) {
        this.status = status;
        this.uid = uid;
    }

    public GeneralConquerVO() {
    }
}
