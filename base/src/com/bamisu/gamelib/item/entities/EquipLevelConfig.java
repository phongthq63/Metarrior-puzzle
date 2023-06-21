package com.bamisu.gamelib.item.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipLevelConfig {
    public List<EquipLevelConfigVO> listAttrItem;
    private Map<String, EquipLevelConfigVO> map = null;

    public EquipLevelConfigVO read(String id) {
        if (map == null) {
            map = new HashMap<>();
            for (EquipLevelConfigVO vo : listAttrItem) {
                map.put(vo.id, vo);
            }
        }

        if (map.containsKey(id)) {
            return map.get(id);
        } else {
            return null;
        }
    }
}
