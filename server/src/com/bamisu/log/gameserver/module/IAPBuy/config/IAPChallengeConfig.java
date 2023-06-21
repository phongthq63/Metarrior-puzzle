package com.bamisu.log.gameserver.module.IAPBuy.config;

import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPChallengeVO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IAPChallengeConfig {
    public List<IAPChallengeVO> list;
    private Map<String,IAPChallengeVO> mapIAPChallenge;

    public IAPChallengeVO readIAPChallengeConfig(String idChallenge){
        if(mapIAPChallenge == null){
            mapIAPChallenge = list.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldObj, newObj) -> newObj));
        }
        return mapIAPChallenge.get(idChallenge);
    }
}
