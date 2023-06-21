package com.bamisu.log.gameserver.module.skill;

/**
 * Create by Popeye on 11:04 AM, 2/26/2020
 */
public enum DamageType {
    PHYSICAL(0, "physical"),
    MAGIC(1, "magic"),
    STANDARD(2, "standard"),
    MANA(3, "mana");

    int intValue;
    String strValue;

    DamageType(int intValue, String strValue) {
        this.intValue = intValue;
        this.strValue = strValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public String getStrValue() {
        return strValue;
    }

    public static DamageType fromIntValue(int value){
        for(DamageType dameType : values()){
            if(dameType.getIntValue() == value) return dameType;
        }

        return null;
    }

    public static DamageType fromStrValue(String strValue){
        for(DamageType dameType : values()){
            if(dameType.getStrValue().equalsIgnoreCase(strValue)) return dameType;
        }

        return null;
    }
}
