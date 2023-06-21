package com.bamisu.log.gameserver.module.WoL.entities;

public class WoLAchievementVO {
    public long uid;
    public String name;
    public int level;
    public int avatarFrame;
    public int status;
    public String avatar;

    public int reward;

    public WoLAchievementVO(long uid, String name, int level, int avatarFrame, int status, String avatar, int reward) {
        this.uid = uid;
        this.name = name;
        this.level = level;
        this.avatarFrame = avatarFrame;
        this.status = status;
        this.avatar = avatar;
        this.reward = reward;
    }

    public WoLAchievementVO() {
    }
}
