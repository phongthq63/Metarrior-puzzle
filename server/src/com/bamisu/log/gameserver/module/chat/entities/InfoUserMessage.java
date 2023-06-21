package com.bamisu.log.gameserver.module.chat.entities;

public class InfoUserMessage {
    public long uid;
    public String avatar;
    public String displayName;
    public short gender;
    public int level;

    public static InfoUserMessage create(long uid, String avatar, String displayName, int level) {
        InfoUserMessage info = new InfoUserMessage();
        info.uid = uid;
        info.avatar = avatar;
        info.displayName = displayName;
        info.level = level;

        return info;
    }
}
