package com.bamisu.log.gameserver.module.campaign.defind;

/**
 * Created by Quach Thanh Phong
 * On 5/27/2022 - 1:04 AM
 */
public enum  ECampaignRankStatus {
    OPEN("0"),
    CLOSE("1");

    private String id;

    ECampaignRankStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
