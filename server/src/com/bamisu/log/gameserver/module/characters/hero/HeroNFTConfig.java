package com.bamisu.log.gameserver.module.characters.hero;

import com.bamisu.log.gameserver.entities.EStatus;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Quach Thanh Phong
 * On 9/22/2022 - 10:14 PM
 */
public class HeroNFTConfig {

    public List<HeroVO> listHero;
    private Map<String,HeroVO> mapHero;

    public HeroVO readHero(String idHero) {
        if(mapHero == null){
            mapHero = listHero.parallelStream().filter(obj -> EStatus.COMMING_SOON.getId() != obj.status).collect(Collectors.toMap(obj -> obj.id, Function.identity(), (objOld, objNew) -> objNew));
        }
        return HeroVO.createHero(mapHero.get(idHero));
    }

}
