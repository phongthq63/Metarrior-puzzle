package com.bamisu.log.gameserver.module.bag.config;

import com.bamisu.log.gameserver.module.bag.config.entities.EnergyChangeVO;

import java.util.List;
import java.util.Map;

public class EnergyConfig {
    public int max;
    public int increaseTime;
    public Map<Integer, Integer> increase;
    public List<EnergyChangeVO> up;
}
