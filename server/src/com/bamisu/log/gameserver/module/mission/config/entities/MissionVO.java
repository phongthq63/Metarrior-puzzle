package com.bamisu.log.gameserver.module.mission.config.entities;

import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.List;

public class MissionVO {
    public String id;
    public short star;
    public float rate;
    public MissionUnlockVO unlock;
    public MissionWinVO win;
    public List<ResourcePackage> reward;
}
