package com.bamisu.log.gameserver.module.vip.entities;

import java.util.List;

public class VipConfig {
    public List<VipVO> vip;

    public VipConfig(){}

    public VipConfig(List<VipVO> vip){
        this.vip = vip;
    }
}
