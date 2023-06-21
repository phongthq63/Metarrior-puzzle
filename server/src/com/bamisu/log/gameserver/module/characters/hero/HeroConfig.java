package com.bamisu.log.gameserver.module.characters.hero;

import com.google.common.collect.Lists;
import com.bamisu.log.gameserver.entities.EStatus;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HeroConfig {
    public List<HeroVO> listHero;
    private Map<String,HeroVO> mapHero;

    public HeroVO readHero(String idHero) {
        if(mapHero == null){
            mapHero = listHero.parallelStream().filter(obj -> EStatus.COMMING_SOON.getId() != obj.status).collect(Collectors.toMap(obj -> obj.id, Function.identity(), (objOld, objNew) -> objNew));
        }
        return HeroVO.createHero(mapHero.get(idHero));
    }

    public List<HeroVO> readListHero(){
        if(mapHero == null){
            mapHero = listHero.parallelStream().filter(obj -> EStatus.COMMING_SOON.getId() != obj.status).collect(Collectors.toMap(obj -> obj.id, Function.identity(), (objOld, objNew) -> objNew));
        }
        return Lists.newArrayList(mapHero.values());
    }

}
