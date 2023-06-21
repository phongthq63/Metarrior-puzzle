package com.bamisu.log.gameserver.module.vip;

public enum RewardType {
    USING_USUALLY("1"),
    USING_ONCE_PER_DAY("2");

    String id;

    RewardType(String id){
        this.id = id;
    }

    RewardType(){}

    public static RewardType fromType(String id){
        for (RewardType value : RewardType.values()) {
            if(value.getType().equalsIgnoreCase(id)) return value;
        }

        return null;
    }

    public String getType(){return id;}

    public void setVip(String id){this.id = id;}
}
