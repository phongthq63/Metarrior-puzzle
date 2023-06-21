package com.bamisu.log.gameserver.module.characters.mboss;

import com.bamisu.log.gameserver.module.characters.mboss.entities.MbossVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Create by Popeye on 9:03 PM, 4/27/2020
 */
public class MbossConfig {
    public List<MbossVO> listMBoss;
    private Map<String,MbossVO> mapMBoss;

    public MbossConfig() {
    }

    public MbossConfig(List<MbossVO> listMBoss) {
        this.listMBoss = listMBoss;
    }

    public MbossVO readMbossVO(String id){
        if(mapMBoss == null){
            mapMBoss = listMBoss.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> newValue));
        }
        return mapMBoss.get(id);
    }
}
