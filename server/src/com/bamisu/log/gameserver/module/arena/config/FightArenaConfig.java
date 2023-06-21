package com.bamisu.log.gameserver.module.arena.config;

import com.bamisu.log.gameserver.module.arena.config.entities.FightArenaVO;
import com.bamisu.log.gameserver.module.arena.config.entities.SearchFightArenaVO;

import java.util.List;

public class FightArenaConfig {
    public String bonus;
    public List<FightArenaVO> win;
    public List<FightArenaVO> lose;
    public List<SearchFightArenaVO> searchFight;

    public FightArenaVO readWinConfig(int point){
        for(FightArenaVO index : win){
            if(index.range.isEmpty()) return index;
            if(index.range.size() > 1){
                if(index.range.get(0) <= point && point <= index.range.get(1)){
                    return index;
                }
            }else {
                if(point >= index.range.get(0)){
                    return index;
                }
            }
        }
        return null;
    }

    public FightArenaVO readLoseConfig(int point){
        for(FightArenaVO index : lose){
            if(index.range.isEmpty()) return index;
            if(index.range.size() > 1){
                if(index.range.get(0) <= point && point <= index.range.get(1)){
                    return index;
                }
            }else {
                if(point >= index.range.get(0)){
                    return index;
                }
            }
        }
        return null;
    }
}
