package com.bamisu.log.gameserver.module.mail.define;

import com.bamisu.gamelib.item.define.ResourceType;

public enum EMailDefine {
    READ(false),
    UNREAD(true);
    boolean status;

    EMailDefine(boolean status){
        this.status = status;
    }

    EMailDefine(){}

    public static EMailDefine fromType(boolean status){
        for (EMailDefine value : EMailDefine.values()) {
            if(value.getStatus() == status) return value;
        }

        return null;
    }

    public boolean getStatus(){return status;}

    public void setStatus(boolean status){this.status = status;}
}
