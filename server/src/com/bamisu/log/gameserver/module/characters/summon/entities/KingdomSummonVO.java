package com.bamisu.log.gameserver.module.characters.summon.entities;

import com.bamisu.gamelib.item.entities.MoneyPackageVO;

import java.util.List;
import java.util.stream.Collectors;

public class KingdomSummonVO {
    public String id;
    public String name;
    public int status;
    public List<MoneyPackageVO> cost;

    public List<MoneyPackageVO> getCostUpdateKingdomDay(){
        return cost.parallelStream().map(obj -> new MoneyPackageVO(obj.id, -obj.amount)).collect(Collectors.toList());
    }
}
