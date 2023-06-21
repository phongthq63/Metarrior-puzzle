package com.bamisu.log.gameserver.module.arena.config;

import com.bamisu.log.gameserver.module.arena.config.entities.BetVO;

import java.util.List;

/**
 * Create by Popeye on 11:12 AM, 4/23/2021
 */
public class PvPOnlineConfig {
    public List<BetVO> bets;

    public BetVO readBetVO(short id){
        for(BetVO betVO : bets){
            if(betVO.id == id){
                return betVO;
            }
        }

        return null;
    }
}
