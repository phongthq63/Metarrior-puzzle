package com.bamisu.log.gameserver.module.hero.define;

public enum ETeamType {
    CAMPAIGN("0"),
    TOWER("1"),
    MONSTER_HUNT("2"),
    MISSION_OUTPOST("3"),
    PVP_OFFLINE("4"),
    DARK_GATE("5"),
    ARENA("6"),
    ARENA_DEFENSE("7"),
    PVP_OFFLINE_DEFENSE("8"),;

    String id;

    ETeamType(String id) {
        this.id = id;
    }

    public static ETeamType fromID(String id){
        for(ETeamType type : ETeamType.values()){
            if(type.id.equals(id)){
                return type;
            }
        }
        return null;
    }
    public static ETeamType fromID(int id){
        for(ETeamType type : ETeamType.values()){
            if(type.id.equals(String.valueOf(id))){
                return type;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }
}
