package com.bamisu.log.gameserver.module.arena.config;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.log.gameserver.module.arena.config.entities.RewardRankArenaVO;

import java.util.List;

public class RewardArenaConfig {
    public List<ResourcePackage> lose;
    public List<ResourcePackage> win;
    public List<RewardRankArenaVO> daily;
    public List<RewardRankArenaVO> season;

    private int maxTop;
}
