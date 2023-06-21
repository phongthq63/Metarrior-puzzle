package com.bamisu.gamelib.entities;

public enum ECacheType {
    USER_ALL_HERO_MODEL("1"),
    USER_MAIN_HERO_MODEL("2"),
    USER_BAG_MODEL("3"),
    USER_FRIEND_HERO_MODEL("4"),
    GUILD_MANAGER_MODEL("5"),
    USER_GUILD_MODEL("6"),
    GUILD_SEARCH_INFO("7"),
    TOWER_MANAGER_MODEL("8"),
    USER_QUEST_MODEL("9"),
    CACHE_RANK_TOWER_MODEL("10"),
    GUILD_MANAGER_STATUS_MODEL("11"),
    MAIL_ADMIN("12"),
    POINT_ARENA_MODEL("13"),
    MAIL_MODEL("14"),
    ARENA_MANAGER_MODEL("16"),
    SERVER_VARIABLE_MODEL("17"),
    USER_BLESSING_MODEL("18"),
    USER_IAP_STORE_MODEL("19"),
    USER_IAP_HOME_MODEL("20"),
    IAP_EVENT_MODEL("21"),;

    String id;

    ECacheType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static ECacheType fromID(String id){
        for(ECacheType type : ECacheType.values()){
            if(type.id.equals(id)){
                return type;
            }
        }
        return null;
    }
}
