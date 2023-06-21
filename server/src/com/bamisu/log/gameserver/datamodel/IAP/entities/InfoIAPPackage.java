package com.bamisu.log.gameserver.datamodel.IAP.entities;

import com.couchbase.client.deps.com.fasterxml.jackson.annotation.JsonProperty;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPPackageVO;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

public class InfoIAPPackage implements IIAPItem {
    public String id;
    public short buy;
    @JsonProperty("tsr")
    public int timeStampRefresh;
    @JsonProperty("tse")
    public int timeStampExsist;


    public static InfoIAPPackage create(String id) {
        InfoIAPPackage infoIAPItem = new InfoIAPPackage();
        infoIAPItem.id = id;
        infoIAPItem.buy = 0;
        infoIAPItem.timeStampRefresh = Utils.getTimestampInSecond();
        infoIAPItem.timeStampExsist = infoIAPItem.timeStampRefresh;

        return infoIAPItem;
    }



    public final void refresh(Zone zone){
        buy = 0;
        timeStampRefresh = Utils.getTimestampInSecond();
    }

    @Override
    public boolean autoIncreasePoint() {
        return false;
    }

    public boolean activeSpecial(){
        return false;
    }


    public final boolean checkTimeRefresh(Zone zone){
        IAPPackageVO packageCf = IAPBuyManager.getInstance().getIAPPackageConfig(id, zone);
        if(!packageCf.canRefresh()){
            return false;
        }
        if(TimeUtils.isTimeTo(ETimeType.fromID(packageCf.timeRefresh.get(0)), timeStampRefresh)){
            return true;
        }

        if(buy > 0 && (id.equals("sky_daily_free") || id.equals("AUN001") || id.equals("AUN002"))){
            return TimeUtils.isTimeTo(ETimeType.fromID(packageCf.timeRefresh.get(1)), timeStampRefresh);
        }
        return false;
    }

    public final int readTimeRefresh(Zone zone){
        IAPPackageVO packageCf = IAPBuyManager.getInstance().getIAPPackageConfig(id, zone);

        int time0 = TimeUtils.getDeltaTimeToTime(ETimeType.fromID(packageCf.timeRefresh.get(0)), timeStampRefresh);
        int time1 = TimeUtils.getDeltaTimeToTime(ETimeType.fromID(packageCf.timeRefresh.get(1)), timeStampRefresh);

        if(buy > 0 && (id.equals("sky_daily_free") || id.equals("AUN001") || id.equals("AUN002"))){
            return Math.min(time0, time1);
        }else {
            return time0;
        }
    }



    public final boolean checkTimeDisapear(Zone zone){
        int index = 0;
        if(buy > 0){
            index = 1;
        }
        return checkTimeDisapear(index, zone);
    }
    private boolean checkTimeDisapear(int index, Zone zone){
        IAPPackageVO packageCf = IAPBuyManager.getInstance().getIAPPackageConfig(id, zone);
        if(packageCf == null) return true;
        if(!packageCf.haveLimitTime() || packageCf.timeExsist.size() <= 0) return false;
        return TimeUtils.isTimeTo(ETimeType.fromID(packageCf.timeExsist.get(index)), timeStampExsist);
    }

    public int readTimeDispear(Zone zone){
        int index = 0;
        if(buy > 0){
            index = 2;
        }
        IAPPackageVO packageCf = IAPBuyManager.getInstance().getIAPPackageConfig(id, zone);
        if(!packageCf.haveLimitTime() || packageCf.timeExsist.size() <= 0) return -1;
        return TimeUtils.getDeltaTimeToTime(ETimeType.fromID(packageCf.timeExsist.get(index)), timeStampExsist);
    }


    public final boolean canBuyMore(Zone zone){
        IAPPackageVO packageCf = IAPBuyManager.getInstance().getIAPPackageConfig(id, zone);
        if(!packageCf.haveLimitBuy()) return true;
        return buy < packageCf.maxBuy;
    }

    public void buy(){
        buy++;

        if(id.equals("sky_daily_free") || id.equals("AUN001") || id.equals("AUN002")){
            timeStampRefresh = Utils.getTimestampInSecond();
        }
        if(id.equals("first_purchase")){
            timeStampExsist = Utils.getTimestampInSecond();
        }
    }
}
