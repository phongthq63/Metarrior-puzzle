package com.bamisu.log.gameserver.module.IAPBuy.config;

import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPPackageVO;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IAPPackageConfig {
    public List<IAPPackageVO> list;
    private Map<String,IAPPackageVO> map;

    public IAPPackageVO readIAPPackageVO(String id){
        if(map == null){
            map = list.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (objOld,objNew) -> objNew));
        }
        return map.get(id);
    }
}
