package com.bamisu.log.gameserver.module.IAPBuy.entities;

import java.util.List;

public class IAPInstanceVO {
    public String id;
    public List<ConditionVO> condition;

    public static IAPInstanceVO create(String id, List<ConditionVO> listCondition){
        IAPInstanceVO iapInstanceVO = new IAPInstanceVO();
        iapInstanceVO.id = id;
        iapInstanceVO.condition = listCondition;

        return iapInstanceVO;
    }
}
