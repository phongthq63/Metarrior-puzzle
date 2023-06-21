package com.bamisu.log.sdk.module.gamethriftclient.IAP.entities;

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
}
