package com.bamisu.log.gameserver.datamodel.arena.entities;

import com.bamisu.gamelib.utils.Utils;

public class RecordArenaInfo {
    public String hashRecord;
    public long uid;
    public long enemy;
    public long win;
    public int point;
    public int timeStamp;

    public static RecordArenaInfo create(String hashRecord, long uid, long enemy, long win, int point) {
        RecordArenaInfo data = new RecordArenaInfo();
        data.hashRecord = hashRecord;
        data.uid = uid;
        data.enemy = enemy;
        data.win = win;
        data.point = point;
        data.timeStamp = Utils.getTimestampInSecond();

        return data;
    }
}
