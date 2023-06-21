package com.bamisu.log.gameserver.module.IAPBuy.config;

import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPChallengeVO;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPPackageVO;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IAPSpecialPackageConfig {
    public List<IAPChallengeVO> listChallenge;
    public List<IAPPackageVO> listPackage;

    private Map<String,IAPChallengeVO> mapChallenge;
    private Map<String,IAPPackageVO> mapPackage;

    public IAPPackageVO readIAPPackageVO(String id){
        if(mapPackage == null){
            mapPackage = listPackage.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (objOld, objNew) -> objNew));
        }
        return mapPackage.get(id);
    }
    public IAPChallengeVO readIAPChallengeVO(String id){
        if(mapChallenge == null){
            mapChallenge = listChallenge.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (objOld, objNew) -> objNew));
        }
        return mapChallenge.get(id);
    }
}
