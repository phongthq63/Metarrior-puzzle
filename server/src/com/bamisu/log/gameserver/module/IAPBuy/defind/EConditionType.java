package com.bamisu.log.gameserver.module.IAPBuy.defind;

import com.bamisu.gamelib.entities.EVip;
import com.bamisu.gamelib.entities.MoneyType;

public enum EConditionType {
    LEVEL_USER("0", ""),
    LEVEL_HERO("27", ""),
    STATION_DUNGEON("37", ""),
    CHAP_DUNGEON("1", ""),
    FLOOR_TOWER("2", ""),
    GET_HERO("26", ""),
    GET_MYTHIC_HERO("3", "MYTHIC"),
    GET_ASCENDED_HERO("4", "ASCENDED"),
    PAYMENT("5", ""),
    SEND_FRIEND_POINT("6", ""),
    ENHANDCE_ITEM("7", ""),
    DO_MISSION("8", ""),
    DO_GUILD_HUNT("9", ""),
    COLLECT_AFK("10", ""),
    UPLEVEL_HERO("11", ""),
    UPSTAR_HERO("38", ""),
    DO_HUNT("12", ""),
    DO_TOWER("13", ""),
    USE_FAST_REWARD("14", ""),
    SUMMON_TAVERN("15", ""),
    DO_ARENA("16", ""),
    DO_CAMPAIGN("17", ""),
    WIN_ARENA("18", ""),
    WIN_MISSION("19", ""),
    WIN_HUNT("20", ""),
    BUY_GENERAL_STORE("21", ""),
    BUY_GUILD_STORE("22", ""),
    BUY_HUNTER_STORE("23", ""),
    GET_ALLIANCE_COIN("24", MoneyType.ALLIANCE_COIN.getId()),
    BLESLING_HERO("25", ""),
    BLESLING_LEVEL("28", ""),
    OPEN_SLOT_BLESSING("34", ""),
    ARENA_POINT("29", ""),
    GOLD_AFK("30", MoneyType.GOLD.getId()),
    LINK_FACEBOOK("31", ""),
    LINK_GOOGLE("32", ""),
    LINK_GAME_CENTER("33", ""),
    LINK_ACCOUNT("34", ""),
    INVITE_USER("35", ""),
    LEVEL_USER_50("36", "50"),
    SEND_FRIEND_REQUEST("39", ""),
    JOIN_GUILD("40", ""),
    CHAT("41", ""),
    LOSE_BATTLE("42", ""),
    RESOURCE_UNLOCK_CELESTIAL("43", "RES1011"),
    HAVE_VIP("44", ""),
    HAVE_VIP_1("45", EVip.ARCHMAGE.getName()),
    HAVE_VIP_2("46", EVip.PROTECTOR.getName());

    private String id;
    private String description;

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    EConditionType(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public static EConditionType fromID(String id){
        for(EConditionType type : EConditionType.values()){
            if(type.id.equals(id)){
                return type;
            }
        }
        return null;
    }
}
