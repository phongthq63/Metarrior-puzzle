package com.bamisu.log.gameserver.module.campaign.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendBuyStoreCampaign extends BaseMsg {

    public ResourcePackage reward;

    public SendBuyStoreCampaign() {
        super(CMD.CMD_BUY_STORE_CAMPAIGN);
    }

    public SendBuyStoreCampaign(short errorCode) {
        super(CMD.CMD_BUY_STORE_CAMPAIGN, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.ID, reward.id);
        data.putInt(Params.AMOUNT, reward.amount);
    }
}
