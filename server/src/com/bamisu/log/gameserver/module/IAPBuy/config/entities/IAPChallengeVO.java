package com.bamisu.log.gameserver.module.IAPBuy.config.entities;

import com.bamisu.log.gameserver.module.IAPBuy.defind.EIAPIncreaseType;

import java.util.List;

public class IAPChallengeVO {
    public String id;
    public String name;
    public double cost;
    public List<IAPAchievementVO> achievement;
    public String timeRefresh;
    public List<String> timeExsist;
    public List<String> exsistCondition;
    public String increaseBy;

    public boolean canRefresh(){
        return timeRefresh != null || timeRefresh.length() > 0;
    }

    public boolean haveLimitTime(){
        return timeExsist != null && timeExsist.size() > 0;
    }

    public boolean haveConditionExsist(){
        return exsistCondition.size() > 0;
    }

    public EIAPIncreaseType increaseAchievementBy(){
        return EIAPIncreaseType.fromId(increaseBy);
    }
}
