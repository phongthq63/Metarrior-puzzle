package com.bamisu.log.gameserver.module.quest.config;

import com.bamisu.log.gameserver.module.quest.config.entities.QuestVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuestConfig {
    public List<QuestVO> daily;
    public List<QuestVO> weekly;
    public List<QuestVO> allTime;

    private Map<String,QuestVO> mapQuest;

    public QuestVO readQuestConfig(String id){
        if(mapQuest == null){
            mapQuest = new HashMap<>();
            mapQuest.putAll(daily.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> newValue)));
            mapQuest.putAll(weekly.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> newValue)));
            mapQuest.putAll(allTime.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> newValue)));
        }
        return mapQuest.get(id);
    }
}
