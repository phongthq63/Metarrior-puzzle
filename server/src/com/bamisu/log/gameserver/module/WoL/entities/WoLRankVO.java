package com.bamisu.log.gameserver.module.WoL.entities;

import java.util.List;

public class WoLRankVO {
    public int area;
    public String name;
    public List<WoLConfigVO> list;

    public WoLRankVO(int area, String name, List<WoLConfigVO> list) {
        this.name = name;
        this.list = list;
        this.area = area;
    }

    public WoLRankVO() {
    }
}
