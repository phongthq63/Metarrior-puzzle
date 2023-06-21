package com.bamisu.log.gameserver.module.characters.hero;

import com.bamisu.log.gameserver.module.characters.hero.entities.CharacterStatsGrowVO;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CharacterStatsGrowConfig {
    public String method;
    public Map<String,Byte> params;
    public List<Integer> breakThoughtList;
    public List<CharacterStatsGrowVO> list;
    private Map<String, CharacterStatsGrowVO> mapCf;

    public CharacterStatsGrowVO readHeroStatsGrowConfig(String idHero){
        if(mapCf == null){
            mapCf = list.parallelStream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (oldValue, newValue) -> newValue));
        }
        return mapCf.getOrDefault(idHero, new CharacterStatsGrowVO());
    }
}
