package com.bamisu.log.gameserver.datamodel.campaign.entities;

/**
 * Create by Popeye on 4:08 PM, 12/28/2020
 */
public class PushNotifyInfo {
    public int platform;
    public String id;

    public PushNotifyInfo() {
    }

    public PushNotifyInfo(int platform, String id) {
        this.platform = platform;
        this.id = id;
    }
}
