package com.bamisu.log.gameserver.datamodel.bag.entities;

import com.bamisu.log.gameserver.module.adventure.entities.FastRewardVO;

public class FastRewardDataVO {
    public int id;
    public int count;

    public FastRewardDataVO(int id, int count) {
        this.id = id;
        this.count = count;
    }

    public FastRewardDataVO(){}

    public FastRewardDataVO(FastRewardVO fastRewardVO){
        this.id = fastRewardVO.id;
        this.count = fastRewardVO.count;
    }
}
