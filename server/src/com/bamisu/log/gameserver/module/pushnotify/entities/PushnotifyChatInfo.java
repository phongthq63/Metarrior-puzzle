package com.bamisu.log.gameserver.module.pushnotify.entities;

/**
 * Create by Popeye on 4:32 PM, 1/19/2021
 */
public class PushnotifyChatInfo {
    public long uid;
    public int sendTime = 0;

    public PushnotifyChatInfo(long uid) {
        this.uid = uid;
    }

    public PushnotifyChatInfo(long uid, int sendTime) {
        this.uid = uid;
        this.sendTime = sendTime;
    }
}
