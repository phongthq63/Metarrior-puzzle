package com.bamisu.log.gameserver.module.vip.defines;

import com.bamisu.log.gameserver.module.vip.RewardType;

public enum ERewardType {
    RECEIVED(false),
    CAN_RECEIVE(true);

    boolean status;

    ERewardType(boolean status){
        this.status = status;
    }

    ERewardType(){}

    public boolean getStatus(){return status;}

    public void setStatus(boolean status){this.status = status;}
}
