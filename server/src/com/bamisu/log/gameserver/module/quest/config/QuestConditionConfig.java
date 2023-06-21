package com.bamisu.log.gameserver.module.quest.config;

import com.bamisu.log.gameserver.module.quest.config.entities.QuestConditionVO;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuestConditionConfig {
    public List<QuestConditionVO> list;
    private Map<String,QuestConditionVO> mapCondition;

    public QuestConditionVO readQuestConditionConfig(String id){
        if(mapCondition == null){
            mapCondition.putAll(list.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> newValue)));
        }

        return mapCondition.get(id);
    }
}
