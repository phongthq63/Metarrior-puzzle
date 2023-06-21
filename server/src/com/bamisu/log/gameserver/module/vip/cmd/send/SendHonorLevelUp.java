package com.bamisu.log.gameserver.module.vip.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

/**
 * Create by Popeye on 5:30 PM, 9/23/2020
 */
public class SendHonorLevelUp extends BaseMsg {
    public int currentLevel;

    public SendHonorLevelUp() {
        super(CMD.CMD_NOTIFY_HONOR_LEVEL_UP);
    }

    public SendHonorLevelUp(int cmdId, short errorCode) {
        super(cmdId, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        data.putInt(Params.LEVEL, currentLevel);
    }
}
