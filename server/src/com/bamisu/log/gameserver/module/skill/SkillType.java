package com.bamisu.log.gameserver.module.skill;

/**
 * Create by Popeye on 3:47 PM, 4/28/2020
 */
public enum SkillType {
    PASSIVE(0, "Passive"),
    ACTIVE(1, "Active");

    private int intValue;
    private String name;

    SkillType(int intValue, String name){
        this.intValue = intValue;
        this.name = name;
    }

    public int getIntValue() {
        return intValue;
    }

    public String getName() {
        return name;
    }

    public static SkillType fromIntValue(int intValue){
        for(SkillType skillType : values()){
            if(skillType.getIntValue() == intValue){
                return skillType;
            }
        }
        return null;
    }

    public static SkillType fromName(String name){
        for(SkillType skillType : values()){
            if(skillType.getName().equalsIgnoreCase(name)){
                return skillType;
            }
        }
        return null;
    }
}
