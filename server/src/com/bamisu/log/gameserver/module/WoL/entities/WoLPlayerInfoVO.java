package com.bamisu.log.gameserver.module.WoL.entities;

public class WoLPlayerInfoVO {
    public long uid;
    public String name;
    public int level;
    public int avatarFrame;
    public String avatar;
    public int stage;

    public WoLPlayerInfoVO(long uid, String name, int level, int avatarFrame, String avatar, int stage) {
        this.uid = uid;
        this.name = name;
        this.level = level;
        this.avatarFrame = avatarFrame;
        this.avatar = avatar;
        this.stage = stage;
    }

    public WoLPlayerInfoVO() {
    }
}
