package com.bamisu.log.gameserver.module.vip;

import com.bamisu.log.gameserver.module.vip.entities.Benefits;
import com.bamisu.log.gameserver.module.vip.entities.VipVO;

public interface IVip {
    public VipVO getVip(String id);
    public Benefits getBenefits();

}
