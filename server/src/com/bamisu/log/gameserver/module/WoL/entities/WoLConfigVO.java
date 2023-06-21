package com.bamisu.log.gameserver.module.WoL.entities;

import java.util.List;

public class WoLConfigVO {
    public int stage;
    public String name;
    public List<WoLContentVO> listChallenges;

    public WoLConfigVO(int stage, String name, List<WoLContentVO> listChallenges) {
        this.name = name;
        this.listChallenges = listChallenges;
        this.stage = stage;
    }

    public WoLConfigVO(){}
}
