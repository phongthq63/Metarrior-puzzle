package com.bamisu.log.gameserver.module.friends.entities;

public class BlockedFriendVO {
    public long uid;
    public String avatar;
    public int level;
    public String name;
    public int power;
    public int avatarFrame;

    public BlockedFriendVO(long uid, String avatar, int level, String name, int power, int avatarFrame) {
        this.uid = uid;
        this.avatar = avatar;
        this.level = level;
        this.name = name;
        this.power = power;
        this.avatarFrame = avatarFrame;
    }

    public BlockedFriendVO() {
    }
}
