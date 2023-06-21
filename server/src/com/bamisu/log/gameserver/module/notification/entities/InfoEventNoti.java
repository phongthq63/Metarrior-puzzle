package com.bamisu.log.gameserver.module.notification.entities;

public class InfoEventNoti {
    public String id;
    public int time;

    public static InfoEventNoti create(String id, int time) {
        InfoEventNoti infoEventNoti = new InfoEventNoti();
        infoEventNoti.id = id;
        infoEventNoti.time = time;

        return infoEventNoti;
    }
}
