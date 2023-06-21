package com.bamisu.log.gameserver.module.IAPBuy.config;

import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPConditionVO;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IAPConditionConfig {
    public List<IAPConditionVO> list;
    private Map<String,IAPConditionVO> mapIAPCondition;

    public IAPConditionVO readIAPConditionVO(String idCondition){
        if(mapIAPCondition == null){
            mapIAPCondition = list.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldObj,newObj) -> newObj));
        }
        return mapIAPCondition.get(idCondition);
    }
}
