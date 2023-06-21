package com.bamisu.log.gameserver.module.quest.config;

import com.bamisu.log.gameserver.module.quest.config.entities.QuestChestVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuestChestConfig {
    public List<QuestChestVO> daily;
    public List<QuestChestVO> weekly;
    public List<QuestChestVO> allTime;
    private Map<String, QuestChestVO> mapChestCong;

    public QuestChestVO readChestQuestConfig(String id){
        if(mapChestCong == null){
            mapChestCong = new HashMap<>();
            mapChestCong.putAll(daily.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> newValue)));
            mapChestCong.putAll(weekly.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> newValue)));
            mapChestCong.putAll(allTime.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> newValue)));
        }
        return mapChestCong.get(id);
    }
}
