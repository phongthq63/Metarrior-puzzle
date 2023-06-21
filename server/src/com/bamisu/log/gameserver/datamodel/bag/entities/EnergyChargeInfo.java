package com.bamisu.log.gameserver.datamodel.bag.entities;

import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.bag.config.EnergyConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnergyChargeInfo {
    public int point;
    public int timeOdd;
    public int timeStamp;

    public int increase = 0;
    public List<String> heros = new ArrayList<>();

    public Map<String,Integer> use = new HashMap<>();



    public static EnergyChargeInfo create() {
        EnergyChargeInfo data = new EnergyChargeInfo();
        data.point = BagManager.getInstance().getEnergyConfig().max;
        data.timeOdd = 0;
        data.timeStamp = Utils.getTimestampInSecond();

        return data;
    }

    public void readEnergy(){
        EnergyConfig energyConfig = BagManager.getInstance().getEnergyConfig();
        int max = energyConfig.max;
        int per = energyConfig.increaseTime;

        if(point >= max){
            timeOdd = 0;
            timeStamp = Utils.getTimestampInSecond();
            return;
        }

        int now = Utils.getTimestampInSecond();
        int increase = (now - timeStamp + timeOdd) / per * this.increase;

        //Tang max do time
        if(point + increase >= max){
            point = max;
            timeOdd = 0;
        }else {
            point = point + increase;
            timeOdd = (now - timeStamp + timeOdd) % per;
        }
        timeStamp = now;
    }

    public void readEnergyHunt(){
        EnergyConfig energyConfig = BagManager.getInstance().getEnergyHuntConfig();
        int max = energyConfig.max;
        int per = energyConfig.increaseTime;

        if(point >= max){
            timeOdd = 0;
            timeStamp = Utils.getTimestampInSecond();
            return;
        }

        int now = Utils.getTimestampInSecond();
        int increase = (now - timeStamp + timeOdd) / per * this.increase;

        //Tang max do time
        if(point + increase >= max){
            point = max;
            timeOdd = 0;
        }else {
            point = point + increase;
            timeOdd = (now - timeStamp + timeOdd) % per;
        }
        timeStamp = now;
    }

    /**
     * Phai read truoc khi thay doi
     * @param point
     */
    public void changeEnergy(int point){
        this.point += point;
    }

    public int readCountUseCharge(String id){
        if(use == null) use = new HashMap<>();
        return use.getOrDefault(id, 0);
    }
    public void useCharge(String id){
        if(use == null) use = new HashMap<>();
        use.put(id, use.getOrDefault(id, 0) + 1);
    }

    public void refreshNewDay(){
        use.clear();
    }

    public void refreshNewWeek(){
        EnergyConfig energyConfig = BagManager.getInstance().getEnergyConfig();
        point = energyConfig.max;
        use.clear();
    }

    public void refreshNewWeekHunt(){
        EnergyConfig energyConfig = BagManager.getInstance().getEnergyHuntConfig();
        point = energyConfig.max;
        use.clear();
    }
}
