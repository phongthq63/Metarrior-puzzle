package com.bamisu.log.gameserver.module.adventure.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendGoToAdventure extends BaseMsg {
    public boolean fastReward;
    public SendGoToAdventure() {
        super(CMD.CMD_GO_TO_ADVENTURE);
    }

    public SendGoToAdventure(short errorCode) {
        super(CMD.CMD_GO_TO_ADVENTURE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        data.putBool(Params.FAST_REWARD, fastReward);
    }
}
