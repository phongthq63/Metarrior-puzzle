package com.bamisu.log.gameserver.module.lucky_draw.config;

import com.bamisu.log.gameserver.module.lucky_draw.config.entities.LuckyDrawVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LuckyDrawConfig {
    public List<LuckyDrawVO> lucky_items;
    private Map<String, LuckyDrawVO> mapLuckyDraw;

    public LuckyDrawVO readLuckyConfig(String id){
        if(mapLuckyDraw == null){
            mapLuckyDraw = new HashMap<>();
            mapLuckyDraw.putAll(lucky_items.parallelStream().collect(Collectors.toMap(obj -> obj.item_id, Function.identity(), (oldValue, newValue) -> newValue)));
        }
        return mapLuckyDraw.get(id);
    }
}
