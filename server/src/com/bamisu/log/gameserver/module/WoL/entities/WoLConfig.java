package com.bamisu.log.gameserver.module.WoL.entities;

import java.util.List;

public class WoLConfig {
    public List<WoLRankVO> listWoL;

    public WoLConfig(List<WoLRankVO> listWoL) {
        this.listWoL = listWoL;
    }

    public WoLConfig(){}
}
