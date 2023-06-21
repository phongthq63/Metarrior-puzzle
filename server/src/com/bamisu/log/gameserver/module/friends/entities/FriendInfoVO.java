package com.bamisu.log.gameserver.module.friends.entities;

public class FriendInfoVO extends FriendDataVO{
    public int level;
    public String avatar;
    public String name;
    public int server;
    public int power;
    public String campaign;
    public int active;
    public int gender;
    public int avatarFrame;

    public FriendInfoVO() {
    }

    public FriendInfoVO(int gender, int avatarFrame, long uid, boolean send, boolean receive, int level, String avatar, String name, int server, int power, String campaign, int active) {
        super(uid, send, receive);
        this.gender = gender;
        this.avatarFrame = avatarFrame;
        this.level = level;
        this.avatar = avatar;
        this.name = name;
        this.server = server;
        this.power = power;
        this.campaign = campaign;
        this.active = active;
    }

    public FriendInfoVO(int level, String avatar, String name, int server, int power, String campaign, int active) {
        this.level = level;
        this.avatar = avatar;
        this.name = name;
        this.server = server;
        this.power = power;
        this.campaign = campaign;
        this.active = active;
    }
}
