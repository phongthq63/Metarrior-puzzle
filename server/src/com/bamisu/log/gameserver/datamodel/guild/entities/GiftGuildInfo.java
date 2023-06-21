package com.bamisu.log.gameserver.datamodel.guild.entities;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.guild.define.EGuildGiftType;

import java.util.ArrayList;
import java.util.List;

public class GiftGuildInfo {
    public String hash;
    public String id;
    public String type;
    public List<ResourcePackage> resources = new ArrayList<>();
    public GiftGuildDescription description;
    public int timeExpert;
    public int timeStamp;

    public static GiftGuildInfo create(String id, EGuildGiftType type, int timeExpert) {
        GiftGuildInfo info = new GiftGuildInfo();
        info.hash = Utils.genGiftGuildHash();
        info.id = id;
        info.type = type.getId();

        switch (type){
            case DAILY:
                info.timeStamp = (int) (Utils.getTimestampInSecond() + Utils.getDeltaSecondsToEndDay() - 86400);
                info.timeExpert = info.timeStamp + 86400;
                break;
            case BUY:
            case UP_LEVEL_GIFT:
            case CREATE:
                info.timeStamp = Utils.getTimestampInSecond();
                info.timeExpert = timeExpert;
                break;
        }

        return info;
    }
    public static GiftGuildInfo create(String id, EGuildGiftType type, GiftGuildDescription description, int timeExpert) {
        GiftGuildInfo info = new GiftGuildInfo();
        info.hash = Utils.genGiftGuildHash();
        info.id = id;
        info.type = type.getId();
        info.description = description;

        switch (type){
            case DAILY:
                info.timeStamp = (int) (Utils.getTimestampInSecond() + Utils.getDeltaSecondsToEndDay() - 86400);
                info.timeExpert = info.timeStamp + 86400;
                break;
            case BUY:
            case UP_LEVEL_GIFT:
            case CREATE:
                info.timeStamp = Utils.getTimestampInSecond();
                info.timeExpert = timeExpert;
                break;
        }

        return info;
    }

    public static GiftGuildInfo create(GiftGuildInfo giftData, List<ResourcePackage> resources) {
        GiftGuildInfo info = new GiftGuildInfo();
        info.hash = giftData.hash;
        info.id = giftData.id;
        info.type = giftData.type;
        info.resources = resources;
        info.description = giftData.description;
        info.timeExpert = giftData.timeExpert;
        info.timeStamp = giftData.timeStamp;

        return info;
    }
}
