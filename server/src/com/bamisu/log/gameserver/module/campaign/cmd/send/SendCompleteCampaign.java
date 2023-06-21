package com.bamisu.log.gameserver.module.campaign.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendCompleteCampaign extends BaseMsg {

    public SendCompleteCampaign() {
        super(CMD.CMD_COMPLETE_STATION_CAMPAIGN);
    }

    public SendCompleteCampaign(short errorCode) {
        super(CMD.CMD_COMPLETE_STATION_CAMPAIGN, errorCode);
    }
}
