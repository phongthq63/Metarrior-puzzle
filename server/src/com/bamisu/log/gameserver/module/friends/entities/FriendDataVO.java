package com.bamisu.log.gameserver.module.friends.entities;

public class FriendDataVO {
    public long uid;
    public boolean send;
    public boolean receive;

    public FriendDataVO(long uid, boolean send, boolean receive) {
        this.uid = uid;
        this.send = send;
        this.receive = receive;
    }

    public FriendDataVO() {
    }
}
