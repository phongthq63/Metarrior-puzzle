package com.bamisu.log.gameserver.module.IAP.defind;

public enum ETimeType {
    HOURS_1("10"),
    HOURS_8("14"),
    HOURS_12("13"),
    NEW_DAY("0"),
    NEW_WEEK("1"),
    NEW_2_WEEK("12"),
    NEW_MONTH("2"),
    NEW_7_DAY("9"),
    DAY_1("8"),
    DAY_7("3"),
    DAY_28("15"),
    DAY_30("4"),
    DAY_33("11"),
    DAY_42("5"),
    DAY_45("6"),
    DAY_90("7");

    String id;

    public String getId() {
        return id;
    }

    ETimeType(String id) {
        this.id = id;
    }

    public static ETimeType fromID(String id){
        for(ETimeType type : ETimeType.values()){
            if(type.id.equals(id)){
                return type;
            }
        }
        return null;
    }
}
