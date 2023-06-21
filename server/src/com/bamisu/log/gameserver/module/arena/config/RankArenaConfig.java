package com.bamisu.log.gameserver.module.arena.config;

import com.bamisu.log.gameserver.module.arena.config.entities.RankArenaVO;

import java.util.List;

public class RankArenaConfig {
    public List<RankArenaVO> rank;

    public RankArenaVO readRank(int point){
        for(RankArenaVO index : rank){
            if(index.range.size() > 1){
                if(index.range.get(0) <= point && point <= index.range.get(1)) return index;
            }else {
                if(point >= index.range.get(0)) return index;
            }
        }
        return rank.get(0);
    }
}
