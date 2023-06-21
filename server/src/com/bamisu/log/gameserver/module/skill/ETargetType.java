package com.bamisu.log.gameserver.module.skill;

/**
 * Create by Popeye on 3:55 PM, 3/24/2020
 */
public enum ETargetType {
    RANDOM(0, "random"),
    BACK(-1, "back"),
    FRONT(-2, "front"),
    LowestHP(-3, "lowest_hp"),
    similar_position(-4, "similar_position");

    int intValue;
    String strValue;

    public int getIntValue() {
        return intValue;
    }

    public String getStrValue() {
        return strValue;
    }

    ETargetType(int intValue, String strValue){
        this.intValue = intValue;
        this.strValue = strValue;
    }

    public static ETargetType fromIntID(int intID){
        for(ETargetType targetType : values()){
            if(targetType.getIntValue() == intID){
                return targetType;
            }
        }

        return null;
    }

    public static ETargetType fromStrValue(String strValue){
        for(ETargetType targetType : values()){
            if(strValue.contains(targetType.getStrValue())){
                return targetType;
            }
        }

        return RANDOM;
    }
}
