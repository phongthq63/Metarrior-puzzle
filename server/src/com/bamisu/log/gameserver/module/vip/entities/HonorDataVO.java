package com.bamisu.log.gameserver.module.vip.entities;

public class HonorDataVO {
    public int id;
    public int status;      //TRUE: can receive    FALSE: Received

    public HonorDataVO(int id, int status) {
        this.id = id;
        this.status = status;
    }

    public HonorDataVO() {
    }
}
