package com.bamisu.log.gameserver.module.quest.defind;

import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;

public enum EQuestType {
    DAILY("0", ETimeType.NEW_DAY.getId()),
    WEEKLY("1", ETimeType.NEW_WEEK.getId()),
    ALL_TIME("2", "");

    String id;
    String idTimeRefresh;

    public String getId() {
        return id;
    }

    public String getIdETimeRefresh() {
        return idTimeRefresh;
    }

    EQuestType(String id, String idTimeRefresh) {
        this.id = id;
        this.idTimeRefresh = idTimeRefresh;
    }

    public static EQuestType fromID(String id){
        for(EQuestType index : EQuestType.values()){
            if(index.id.equals(id)){
                return index;
            }
        }
        return null;
    }
}
