package com.bamisu.log.gameserver.module.ingame.entities.actor;

/**
 * Create by Popeye on 5:49 PM, 2/27/2020
 */
public enum ActionID {
    SKILLING(0, "Dùng skill"),
    BEATEN(1, "Bị đánh"),
    APPLY_EFFECT(2, "Bị dính hiệu ứng"),
    REMOVED_EFFECT(3, "Hết hiệu ứng"),
    DODGE(4, "Né"),
    MISS(5, "Đánh trượt"),
    DIE(6, "Chết"),
    HEALTH_CHANGE(7, "Máu thay đổi"),
    ENERGY_CHANGE(8, "Năng lượng thay đổi"),
    TANK(9, "Tank hộ"),
    RETURN(10, "Được hồi sinh"),
    SHIELD_CHANGE(11, "Thay đổi máu ảo");

    int intValue;
    String desc;

    ActionID(int intValue, String desc) {
        this.intValue = intValue;
        this.desc = desc;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
