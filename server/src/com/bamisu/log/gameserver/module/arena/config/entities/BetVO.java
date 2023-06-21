package com.bamisu.log.gameserver.module.arena.config.entities;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.item.entities.MoneyPackageVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 11:12 AM, 4/23/2021
 */
public class BetVO {
    public short id;
    public List<ResourcePackage> resources;

    private List<MoneyPackageVO> minusResources;
    public List<MoneyPackageVO> readMinusResources(){
        if(minusResources == null){
            minusResources = new ArrayList<>();
            MoneyPackageVO tmpResourcePackage = null;
            for(ResourcePackage resourcePackage : resources){
                tmpResourcePackage = new MoneyPackageVO(resourcePackage.id, -Math.abs(resourcePackage.amount));
            }
            minusResources.add(tmpResourcePackage);
        }

        return minusResources;
    }
}
