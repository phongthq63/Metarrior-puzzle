package com.bamisu.log.gameserver.module.friends.define;

public enum EStatusHeartPoint {
    SENT(false),
    UNSENT(true);
    boolean status;

    EStatusHeartPoint(boolean status){
        this.status = status;
    }

    EStatusHeartPoint(){}

    public static EStatusHeartPoint fromType(boolean status){
        for (EStatusHeartPoint value : EStatusHeartPoint.values()) {
            if(value.getStatus() == status) return value;
        }

        return null;
    }

    public boolean getStatus(){return status;}

    public void setStatus(boolean status){this.status = status;}
}
