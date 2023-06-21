package com.bamisu.log.gameserver.module.campaign.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendFightCampaign extends BaseMsg {

    public SendFightCampaign() {
        super(CMD.FIGHT_MAIN_CAMPAIGN);
    }

    public SendFightCampaign(short errorCode) {
        super(CMD.FIGHT_MAIN_CAMPAIGN, errorCode);
    }
}
