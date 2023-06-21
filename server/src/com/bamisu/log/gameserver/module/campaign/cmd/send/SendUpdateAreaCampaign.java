package com.bamisu.log.gameserver.module.campaign.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendUpdateAreaCampaign extends BaseMsg {

    public SendUpdateAreaCampaign() {
        super(CMD.CMD_UPDATE_AREA_CAMPAIGN);
    }

    public SendUpdateAreaCampaign(short errorCode) {
        super(CMD.CMD_UPDATE_AREA_CAMPAIGN, errorCode);
    }
}
