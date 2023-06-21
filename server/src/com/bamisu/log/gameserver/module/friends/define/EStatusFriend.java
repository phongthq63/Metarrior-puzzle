package com.bamisu.log.gameserver.module.friends.define;

import com.bamisu.log.gameserver.module.mail.define.EMailDefine;

public enum EStatusFriend {
    ACTIVE(0);
    int status;

    EStatusFriend(int status){
        this.status = status;
    }

    EStatusFriend(){}

    public static EStatusFriend fromType(int status){
        for (EStatusFriend value : EStatusFriend.values()) {
            if(value.getStatus() == status) return value;
        }

        return null;
    }

    public int getStatus(){return status;}

    public void setStatus(int status){this.status = status;}
}
