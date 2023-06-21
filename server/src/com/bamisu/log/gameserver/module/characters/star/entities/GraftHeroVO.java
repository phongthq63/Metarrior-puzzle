package com.bamisu.log.gameserver.module.characters.star.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GraftHeroVO {
    public short star;
    public List<HeroInfoGraftVO> hero;
    public List<ResourcePackage> resource;
    public int time;

    public List<HeroInfoGraftVO> readCostUpdateStarHero(){
        List<HeroInfoGraftVO> list = new ArrayList<>();
        for(HeroInfoGraftVO cf : hero){
            for(int i = 0; i < cf.amount; i++){
                list.add(cf);
            }
        }

        return list;
    }

    public List<ResourcePackage> readResourceCostUpdateStarHero(){
        return resource.stream()
                .map(resourcePackage -> new ResourcePackage(resourcePackage.id, -resourcePackage.amount))
                .collect(Collectors.toList());
    }
}
