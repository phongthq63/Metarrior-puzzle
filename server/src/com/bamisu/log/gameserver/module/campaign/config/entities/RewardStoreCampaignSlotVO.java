package com.bamisu.log.gameserver.module.campaign.config.entities;

import com.bamisu.gamelib.item.entities.MoneyPackageVO;
import com.bamisu.gamelib.entities.ResourcePackage;

import java.util.List;

public class RewardStoreCampaignSlotVO {
    public byte slot;
    public List<RewardChapCampaignVO> reward;
    public MoneyPackageVO cost;
    public int maxbuy;
    public String timeRefresh;
}
