package com.bamisu.log.gameserver.module.vip.defines;

public enum EGiftVip {
    MAX_HERO_SLOT("0"),
    FREE_DIAMOND("1"),
    EXTRA_MISSION("2"),
    EXTRA_REFRESH_MISSION("3"),
    AFK_REWARD_HARVESTING("4"),
    MONSTER_HUNT_MORE_TIME_PLAY("5"),
    MONSTER_HUNT_FREE_REFRESH("6"),
    RECEIVE_FRIENDSHIP_HEARTS("7"),
    MERCENARIES_USER("8"),
    FREE_TURN_FAST_REWARD("9"),
    FREE_BATTLES_IN_ARENA("10");

    String id;

    public String getId() {
        return id;
    }

    EGiftVip(String id) {
        this.id = id;
    }

    public static EGiftVip fromID(String id){
        for(EGiftVip type : EGiftVip.values()){
            if(type.id.equals(id)){
                return type;
            }
        }
        return null;
    }
}
