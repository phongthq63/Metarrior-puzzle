package com.bamisu.log.gameserver.module.mission.config;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.log.gameserver.module.mission.config.entities.MissionVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MissionConfig {
    public int redo;
    public ResourcePackage fightCost;
    public List<MissionVO> listMission;

    private Map<String,MissionVO> mapMission;



    public List<ResourcePackage> readCostFight(){
        List<ResourcePackage> list = new ArrayList<>();
        list.add(new ResourcePackage(fightCost.id, -fightCost.amount));

        return list;
    }

    public MissionVO readMission(String id){
        if(mapMission == null){
            mapMission = listMission.stream().
                    collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> newValue));
        }
        return mapMission.get(id);
    }
}
