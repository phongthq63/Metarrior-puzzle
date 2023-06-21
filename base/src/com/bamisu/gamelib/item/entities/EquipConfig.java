package com.bamisu.gamelib.item.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipConfig {
    public int sizeBag;
    public int maxStar;
    public List<EquipConfigVO> listEquip;
    private Map<String, EquipConfigVO> mapEquip = null;

    public EquipConfigVO readEquip(String id){
        if(mapEquip == null){
            mapEquip = new HashMap<>();
            for(EquipConfigVO equipConfigVO : listEquip){
                mapEquip.put(equipConfigVO.id, equipConfigVO);
            }
        }

        return mapEquip.get(id);
    }
}
