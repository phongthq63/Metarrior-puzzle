package com.bamisu.log.gameserver.datamodel;

public class InfoConfigActive {
    public String id;
    public boolean active;
    public int timeStamp;

    public static InfoConfigActive create(String id) {
        InfoConfigActive data = new InfoConfigActive();
        data.id = id;
        data.active = false;
        data.timeStamp = -1;

        return data;
    }
}
