package com.bamisu.log.gameserver.datamodel.bag.entities;

import com.bamisu.log.gameserver.datamodel.campaign.entities.UserMainCampaignDetail;

public class CampaignFinishVO {
    public UserMainCampaignDetail userMainCampaignDetail;
    public String time;

    public CampaignFinishVO(UserMainCampaignDetail userMainCampaignDetail, String time) {
        this.userMainCampaignDetail = userMainCampaignDetail;
        this.time = time;
    }

    public CampaignFinishVO(){
    }
}
