package com.bamisu.log.gameserver.module.IAPBuy.config.entities;

import com.bamisu.log.gameserver.module.IAPBuy.defind.EIAPPackageType;
import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.List;

public class IAPPackageVO {
    public String id;
    public String giftName;
    public String name;
    public float cost;
    public List<ResourcePackage> reward;
    public short maxBuy;
    public List<String> timeExsist;
    public List<String> timeRefresh;
    public List<String> exsistCondition;
    public List<String> rewardCondition;
    public String description;

    public IAPPackageVO() {
    }

    public boolean haveLimitBuy(){
        return maxBuy > 0;
    }

    public boolean haveLimitTime(){
        return timeExsist.size() > 0 || timeExsist.parallelStream().anyMatch(obj -> !obj.isEmpty());
    }

    public boolean canRefresh(){
        return timeRefresh.size() > 0;
    }

    public boolean haveConditionExsist(){
        return exsistCondition.size() > 0;
    }

    public boolean haveConditionReward(){
        return rewardCondition.size() > 0;
    }

    public String readType(){
        if(haveLimitBuy() && haveLimitBuy() && canRefresh()){
            return EIAPPackageType.BUY_TIME_REFRESH.getId();
        }else if(haveLimitBuy() && haveLimitBuy()){
            return EIAPPackageType.BUY_TIME_REFRESH.getId();
        }else if(haveLimitBuy() && canRefresh()){
            return EIAPPackageType.BUY_REFRESH.getId();
        }else if(haveLimitTime() && canRefresh()){
            return EIAPPackageType.TIME_REFRESH.getId();
        }else if(haveLimitBuy()){
            return EIAPPackageType.BUY.getId();
        }else if(haveLimitTime()){
            return EIAPPackageType.TIME.getId();
        }else if(canRefresh()){
            return EIAPPackageType.REFRESH.getId();
        }else {
            return EIAPPackageType.EXTANT.getId();
        }
    }
}
