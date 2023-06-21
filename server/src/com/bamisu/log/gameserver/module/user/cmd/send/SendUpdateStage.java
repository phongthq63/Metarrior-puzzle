package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendUpdateStage extends BaseMsg {
    public SendUpdateStage() {
        super(CMD.CMD_UPDATE_STAGE);
    }

    public SendUpdateStage(short errorCode) {
        super(CMD.CMD_UPDATE_STAGE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
