package com.bamisu.gamelib.item.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipLevelConfigVO {
    public String id;
    public int maxLevel;
    public List<EquipLevelVO> listLevel;
    private Map<Integer, EquipLevelVO> mapLevel = null;

    public EquipLevelVO readLevel(int level) {
        if (mapLevel == null) {
            mapLevel = new HashMap<>();
            for (EquipLevelVO vo : listLevel) {
                mapLevel.put(vo.level, vo);
            }
        }

        return mapLevel.get(level);
    }
}
