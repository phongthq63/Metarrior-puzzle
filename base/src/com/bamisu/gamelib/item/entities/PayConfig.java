package com.bamisu.gamelib.item.entities;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PayConfig {
    public List<PayEquipVO> payEquip;
    public List<PayGemVO> payGem;

    private Map<Integer,PayEquipVO> mapCostFusionEquip;
    private Map<Integer,PayGemVO> mapCostFusionStone;


    public PayEquipVO readPayFusionHeroEquip(int star){
        if(mapCostFusionEquip == null){
            mapCostFusionEquip = payEquip.stream().
                    collect(Collectors.toMap(obj -> obj.star, Function.identity(), (oldValue,newValue) -> newValue));
        }
        return mapCostFusionEquip.get(star);
    }
    public PayGemVO readPayFusionStone(int star){
        if(mapCostFusionStone == null){
            mapCostFusionStone = payGem.stream().
                    collect(Collectors.toMap(obj -> obj.level, Function.identity(), (oldValue,newValue) -> newValue));
        }
        return mapCostFusionStone.get(star);
    }
}
