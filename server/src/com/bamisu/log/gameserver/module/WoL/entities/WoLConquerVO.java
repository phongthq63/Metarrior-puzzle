package com.bamisu.log.gameserver.module.WoL.entities;

import java.util.List;

public class WoLConquerVO {
    public int stage;
    public List<GeneralConquerVO> listConquer;

    public WoLConquerVO(int stage, List<GeneralConquerVO> listConquer) {
        this.stage = stage;
        this.listConquer = listConquer;
    }

    public WoLConquerVO() {
    }
}
