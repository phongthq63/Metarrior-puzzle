package com.bamisu.log.gameserver.module.WoL.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;

public class SendWoLReceiveReward extends BaseMsg {
    public ResourcePackage resourcePackage;
    public SendWoLReceiveReward() {
        super(CMD.CMD_WOL_RECEIVE_REWARD);
    }

    public SendWoLReceiveReward(short errorCode) {
        super(CMD.CMD_WOL_RECEIVE_REWARD, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        data.putSFSObject(Params.RESOURCE,resourcePackage.toSFSObject());
    }
}
