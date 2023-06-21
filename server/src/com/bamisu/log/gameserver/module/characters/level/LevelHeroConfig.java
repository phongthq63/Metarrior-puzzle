package com.bamisu.log.gameserver.module.characters.level;

import com.bamisu.gamelib.item.entities.MoneyPackageVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LevelHeroConfig {
    public Map<Short,List<MoneyPackageVO>> levelup = new HashMap<>();
    public Map<Short,List<MoneyPackageVO>> breakLimit = new HashMap<>();


    //Da sap xep
    public List<Short> readLevelBreakThought(){
        return breakLimit.keySet().parallelStream().sorted().collect(Collectors.toList());
    }
}
