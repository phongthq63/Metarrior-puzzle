package com.bamisu.log.gameserver.module.tower.entities;

public class FloorRankTowerInfo {
    public long uid;
    public String aid;
    public int frame;
    public String name;
    public int level;
    public long power;
    public short floor;
    public int timeStamp;

    public static FloorRankTowerInfo create(long uid, String aid, int frame, String name, int level, long power, short floor, int timeStamp) {
        FloorRankTowerInfo floorRankTowerInfo = new FloorRankTowerInfo();
        floorRankTowerInfo.uid = uid;
        floorRankTowerInfo.aid = aid;
        floorRankTowerInfo.frame = frame;
        floorRankTowerInfo.name = name;
        floorRankTowerInfo.level = level;
        floorRankTowerInfo.power = power;
        floorRankTowerInfo.floor = floor;
        floorRankTowerInfo.timeStamp = timeStamp;

        return floorRankTowerInfo;
    }
}
