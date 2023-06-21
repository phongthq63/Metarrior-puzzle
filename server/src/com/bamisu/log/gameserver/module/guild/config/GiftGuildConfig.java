package com.bamisu.log.gameserver.module.guild.config;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.log.gameserver.module.guild.config.entities.GiftBoxGuildVO;
import com.bamisu.log.gameserver.module.guild.config.entities.GiftGuildVO;

import java.util.List;

public class GiftGuildConfig {
    public List<GiftGuildVO> up;
    public List<ResourcePackage> create;
    public List<ResourcePackage> daily;
    public List<GiftBoxGuildVO> gift;
}
