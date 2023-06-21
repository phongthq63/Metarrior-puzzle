package com.bamisu.log.gameserver.datamodel.IAP.event.entities;

import com.bamisu.gamelib.utils.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InfoIAPSale {
    public String id;
    public String idSale;
    public float cost;
    public int deltaTime;
    public int timeStamp;
    public Set<Long> target = new HashSet<>();

    public static InfoIAPSale create(String id, String idSale, float cost, int deltaTime, List<Long> target){
        InfoIAPSale infoIAPSale = new InfoIAPSale();
        infoIAPSale.id = id;
        infoIAPSale.idSale = idSale;
        infoIAPSale.cost = cost;
        infoIAPSale.deltaTime = deltaTime;
        infoIAPSale.timeStamp = Utils.getTimestampInSecond();
        infoIAPSale.target.addAll(target);

        return infoIAPSale;
    }

    public boolean haveSale(long uid){
        //Kiem tra thoi gian sale gioi han
        if(haveLimitTime()){
            if(!isTimeDisapear()) return false;
        }
        if(target.isEmpty()) return true;
        return target.contains(uid);
    }

    public boolean haveLimitTime(){
        return deltaTime >= 0;
    }

    public boolean isTimeDisapear(){
        if(deltaTime < 0) return false;
        return deltaTime - (Utils.getTimestampInSecond() - timeStamp) <= 0;
    }

    public int readTimeDisapear(){
        return deltaTime - (Utils.getTimestampInSecond() - timeStamp);
    }

    public float readCost(){
        return cost;
    }
}
