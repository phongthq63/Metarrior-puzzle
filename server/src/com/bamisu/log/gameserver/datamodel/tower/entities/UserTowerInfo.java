package com.bamisu.log.gameserver.datamodel.tower.entities;

import com.bamisu.gamelib.utils.Utils;

public class UserTowerInfo {
    public long uid;
    public short floor;
    public int timeStamp;

    public static UserTowerInfo create(long uid, int floor) {
        UserTowerInfo userTowerInfo = new UserTowerInfo();
        userTowerInfo.uid = uid;
        userTowerInfo.floor = (short) floor;
        userTowerInfo.timeStamp = Utils.getTimestampInSecond();

        return userTowerInfo;
    }

    public static UserTowerInfo create(UserTowerInfo userTowerInfo) {
        UserTowerInfo dulicate = new UserTowerInfo();
        dulicate.uid = userTowerInfo.uid;
        dulicate.floor = userTowerInfo.floor;
        dulicate.timeStamp = userTowerInfo.timeStamp;

        return userTowerInfo;
    }
}
