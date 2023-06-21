package com.bamisu.log.gameserver.module.hunt.config;

import com.bamisu.log.gameserver.module.hunt.config.entities.RefreshHuntVO;

import java.util.List;

public class RefreshHuntConfig {
    public short free;
    public short ads;
    public List<RefreshHuntVO> list;

    public short readFreeRefreshHunt(){
        return (short) (free + ads);
    }
}
