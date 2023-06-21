package com.bamisu.log.gameserver.module.friends.define;

public enum EHeartPoint {
    SEND_HEART_POINT(1);
    int point;

    EHeartPoint(int point){
        this.point = point;
    }

    EHeartPoint(){}


    public int getPoint(){return point;}

}
