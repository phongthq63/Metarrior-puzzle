package com.bamisu.log.gameserver.module.characters.summon.entities;

import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.gamelib.entities.TokenResourcePackage;
import com.bamisu.gamelib.item.define.SpecialItem;
import com.bamisu.gamelib.entities.RandomObj;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.item.entities.IResourcePackage;
import com.bamisu.log.gameserver.module.nft.defind.ETokenBC;

import java.util.*;

public class SummonVO {
    public String id;
    public String type;
    public short bonusPoint = 0;
    public int timeFree = -1;
    public List<CostSummonVO> cost = new ArrayList<>();
    public List<RewardHeroVO> rate = new ArrayList<>();

    private List<TokenResourcePackage> chargeResource(String resourceType, int count){
        List<TokenResourcePackage> resourcePackages = new ArrayList<>();
        for(CostSummonVO costVO : cost){
            if(costVO.money.equals(resourceType)){
                for (ResourceVO vo: costVO.value) {
                    TokenResourcePackage resourcePackage = new TokenResourcePackage(vo.type, -(vo.quantity * count));
                    resourcePackages.add(resourcePackage);
                }

                return resourcePackages;
            }
        }

        return new ArrayList<>();
    }

    public List<TokenResourcePackage> getResource(MoneyType moneyType, int count) {
        return chargeResource(moneyType.getId(), count);
    }
    public List<TokenResourcePackage> getResource(SpecialItem specialItemType, int count) {
        return chargeResource(specialItemType.getId(), count);
    }
    public List<TokenResourcePackage> getResource(ETokenBC tokenType, int count) {
        return chargeResource(tokenType.getId(), count);
    }

    public List<String> getResourceType() {
        List<String> type = new ArrayList<>();
        if (cost == null) return type;

        for (CostSummonVO costVO : cost) {
            type.add(costVO.money);
        }
        return type;
    }

    public List<List<ResourcePackage>> getResource(int count) {
//        return cost.get(count);
        return new ArrayList<>();
    }

    public List<RandomObj> getListRateSummonType(){
        List<RandomObj> listObj = new ArrayList<>();
        for(RewardHeroVO index : rate){
            listObj.add(new RandomObj(index.star, index.rate));
        }
        return listObj;
    }
}
