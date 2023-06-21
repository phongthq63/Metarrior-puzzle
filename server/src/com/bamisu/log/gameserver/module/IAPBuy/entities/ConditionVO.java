package com.bamisu.log.gameserver.module.IAPBuy.entities;

public class ConditionVO {
    public String id;
    public int count;

    public ConditionVO() {
    }

    public ConditionVO(String id, int count) {
        this.id = id;
        this.count = count;
    }

    public static ConditionVO create(String id, int count){
        ConditionVO condition = new ConditionVO();
        condition.id = id;
        condition.count = count;

        return condition;
    }
}
