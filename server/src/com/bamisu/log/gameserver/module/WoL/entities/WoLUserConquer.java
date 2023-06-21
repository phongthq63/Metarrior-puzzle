package com.bamisu.log.gameserver.module.WoL.entities;

import java.util.List;

public class WoLUserConquer {
    public int stage;
    public List<WoLChallengeVO> listChallenges;

    public WoLUserConquer(int stage, List<WoLChallengeVO> listChallenges) {
        this.stage = stage;
        this.listChallenges = listChallenges;
    }

    public WoLUserConquer() {
    }
}
