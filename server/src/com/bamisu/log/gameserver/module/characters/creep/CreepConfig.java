package com.bamisu.log.gameserver.module.characters.creep;

import com.bamisu.log.gameserver.module.characters.creep.entities.CreepVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CreepConfig {
    public List<CreepVO> listCreep = new ArrayList<>();
    private Map<String,CreepVO> mapCreep;

    public CreepVO readCreepConfig(String id){
        if(mapCreep == null){
            mapCreep = listCreep.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> newValue));
        }
        return mapCreep.get(id);
    }
}
