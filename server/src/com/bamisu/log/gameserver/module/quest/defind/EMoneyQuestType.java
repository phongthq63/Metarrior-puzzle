package com.bamisu.log.gameserver.module.quest.defind;

public enum  EMoneyQuestType {
    DAILY_POINT("RES1019"),
    WEEKLY_POINT("RES1020");


    EMoneyQuestType(String id) {
        this.id = id;
    }

    String id;

    public String getId() {
        return id;
    }

    public static EMoneyQuestType fromID(String id){
        for(EMoneyQuestType type : EMoneyQuestType.values()){
            if(type.id.equals(id)){
                return type;
            }
        }
        return null;
    }
}
