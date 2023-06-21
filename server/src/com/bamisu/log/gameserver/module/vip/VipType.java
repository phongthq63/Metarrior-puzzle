package com.bamisu.log.gameserver.module.vip;

public enum VipType {
    VIP_HONOR("1"),
    VIP_GOLD("2"),
    VIP_PLATINUM("3");

    String id;

    VipType(String id){
        this.id = id;
    }

    VipType(){}

    public static VipType fromStringValue(String id){
        for (VipType value : VipType.values()) {
            if(value.getVip().equalsIgnoreCase(id)) return value;
        }

        return null;
    }

    public String getVip(){return id;}

    public void setVip(String id){this.id = id;}
}
