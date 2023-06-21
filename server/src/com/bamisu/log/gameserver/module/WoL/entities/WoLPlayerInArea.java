package com.bamisu.log.gameserver.module.WoL.entities;

import java.util.List;

public class WoLPlayerInArea {
    public int area;
    public List<WoLPlayerInfoVO> list;

    public WoLPlayerInArea(int area, List<WoLPlayerInfoVO> list) {
        this.area = area;
        this.list = list;
    }

    public WoLPlayerInArea() {
    }
}
