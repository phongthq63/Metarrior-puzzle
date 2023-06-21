package com.bamisu.log.gameserver.module.vip.entities;

import com.bamisu.gamelib.entities.EVip;

public class TimeVipVO {
    public EVip eVip;
    public long time;

    public TimeVipVO(EVip eVip, long time) {
        this.eVip = eVip;
        this.time = time;
    }

    public TimeVipVO() {
    }
}
