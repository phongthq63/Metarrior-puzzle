package com.bamisu.log.gameserver.datamodel.guild.entities;

public class GiftGuildDescription {
    public long uid;
    public String idPackage;
    public int level;

    public static GiftGuildDescription create(long uid, String idPackage) {
        GiftGuildDescription data = new GiftGuildDescription();
        data.uid = uid;
        data.idPackage = idPackage;

        return data;
    }

    public static GiftGuildDescription create(int level) {
        GiftGuildDescription data = new GiftGuildDescription();
        data.level = level;

        return data;
    }
}
