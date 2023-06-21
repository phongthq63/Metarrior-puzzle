package com.bamisu.log.gameserver.module.vip.entities;

import java.util.List;

public class VipVO {
    public String id;
    public String name;
    public boolean pay;
    public List<VipElements> elements;


    public VipVO(String id, String name, boolean pay, List<VipElements> elements) {
        this.id = id;
        this.name = name;
        this.pay = pay;
        this.elements = elements;
    }

    public VipVO() {
    }


    public VipVO(VipVO vipVO) {
        this.elements = vipVO.elements;
        this.pay = vipVO.pay;
        this.id  = vipVO.id;
        this.name = vipVO.name;
    }
}
