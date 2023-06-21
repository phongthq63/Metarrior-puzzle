package com.bamisu.log.gameserver.module.WoL.entities;

import java.util.List;

public class WoLPlayerAchievementVO {
    public int area;
    public List<WoLPlayerInStageVO> listPlayer; //stage - id player

    public WoLPlayerAchievementVO(int area, List<WoLPlayerInStageVO> listPlayer) {
        this.area = area;
        this.listPlayer = listPlayer;
    }

    public WoLPlayerAchievementVO() {
    }
}
