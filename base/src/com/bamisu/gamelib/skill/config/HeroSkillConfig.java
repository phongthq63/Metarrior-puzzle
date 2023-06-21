package com.bamisu.gamelib.skill.config;

import com.bamisu.gamelib.skill.config.entities.BaseSkillInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create by Popeye on 11:48 AM, 12/25/2019
 */
public class HeroSkillConfig {
    public List<BaseSkillInfo> list;
    private Map<String, BaseSkillInfo> map;
    private Map<String, List<BaseSkillInfo>> mapGroup;

    public HeroSkillConfig() {
    }

    public HeroSkillConfig(List<BaseSkillInfo> list) {
        this.list = list;
    }

    public void build(){
        buildMap();
        buildGroup();
    }

    private void buildGroup() {
        mapGroup = new HashMap<>();
        for(BaseSkillInfo baseSkillInfo : list){
            if(baseSkillInfo.group != null && baseSkillInfo.group.startsWith("G")){
                if(!mapGroup.containsKey(baseSkillInfo.group)){
                    mapGroup.put(baseSkillInfo.group, new ArrayList<>());
                }
                mapGroup.get(baseSkillInfo.group).add(baseSkillInfo);
            }
        }
    }

    public void buildMap(){
        map = list.stream().collect(Collectors.toMap(BaseSkillInfo::getId, BaseSkillInfo::readMe));
    }

    public Map<String, List<BaseSkillInfo>> getMapGroup() {
        return mapGroup;
    }

    public Map<String, BaseSkillInfo> readMap(){
        return map;
    }
}
