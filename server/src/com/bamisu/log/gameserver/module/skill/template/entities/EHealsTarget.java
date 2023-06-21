package com.bamisu.log.gameserver.module.skill.template.entities;

/**
 * Create by Popeye on 8:23 PM, 6/3/2020
 */
public enum EHealsTarget {
    CUSTOM("custom"), //phải truyền vào khi gọi skill
    ME_RANDOM("me_random"), //đồng minh ngẫu nhiên, nếu không đủ số lượng đồng minh thì lấy cả bản thân
    ME_HA("me_ha"), //đồng mạnh nhất (có STR hoặc INT cao nhất)
    ALLY_LOWEST_HP("ally_lowest_hp"), //đồng minh % máu ít nhất, nếu còn 1 mình thì lấy luôn bản thân
    ENEMY_RANDOM("enemy_random"),
    ME("me"),   //lấy bản thân
    SAGE("sage"),   //pháp sư
    CELESTIAL("celestial"),   //linh thú
    JUST_DIE("just_die");   //vừa chết

    private String strValue;

    EHealsTarget(String strValue) {
        this.strValue = strValue;
    }

    public String getStrValue() {
        return strValue;
    }

    public static EHealsTarget fromStr(String name) {
        for (EHealsTarget type : values()) {
            if (name.contains(type.strValue)) return type;
        }

        return null;
    }
}
