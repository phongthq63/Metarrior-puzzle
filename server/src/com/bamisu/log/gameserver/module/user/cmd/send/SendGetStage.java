package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

import java.util.Collection;

public class SendGetStage extends BaseMsg {
    public String stage;
    public Collection<Integer> stageV2;
    public SendGetStage() {
        super(CMD.CMD_GET_STAGE);
    }

    public SendGetStage(short errorCode) {
        super(CMD.CMD_GET_STAGE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        data.putUtfString(Params.STAGE, stage);
        data.putIntArray(Params.STAGE_V2, stageV2);
    }
}
