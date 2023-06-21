package com.bamisu.log.gameserver.module.hunt.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendRewardHunt extends BaseMsg {

    public SendRewardHunt() {
        super(CMD.CMD_REWARD_HUNT);
    }

    public SendRewardHunt(short errorCode) {
        super(CMD.CMD_REWARD_HUNT, errorCode);
    }
}
