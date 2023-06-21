package com.bamisu.log.gameserver.module.characters.summon.entities;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.TokenResourcePackage;

import java.util.ArrayList;
import java.util.List;

public class CostSummonVO {
    public String money;
    public List<ResourceVO> value;

    public List<TokenResourcePackage> chargeResource(int count){
        List<TokenResourcePackage> resourcePackages = new ArrayList<>();
        for (ResourceVO resourceVO : this.value) {
            TokenResourcePackage resourcePackage = new TokenResourcePackage(resourceVO.type, -(resourceVO.quantity * count));
            resourcePackages.add(resourcePackage);
        }

        return resourcePackages;
    }
}
