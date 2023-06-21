package com.bamisu.log.gameserver.module.skill.template.entities;

/**
 * Create by Popeye on 8:11 PM, 6/3/2020
 */
public enum EHealsType {
    HP("hp"),
    EP("ep"),
    REVIVAL("revival"),
    SHIELD_ALL("shield_all");

    private String strValue;

    EHealsType(String strValue) {
        this.strValue = strValue;
    }

    public String getStrValue() {
        return strValue;
    }

    public static EHealsType fromStr(String name) {
        for (EHealsType type : values()) {
            if (type.strValue.equalsIgnoreCase(name)) return type;
        }

        return null;
    }
}
