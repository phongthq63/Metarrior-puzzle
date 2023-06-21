package com.bamisu.log.gameserver.module.invite.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendRewardInviteCode extends BaseMsg {

    public SendRewardInviteCode() {
        super(CMD.CMD_GET_REWARD_INVITE_CODE);
    }

    public SendRewardInviteCode(short errorCode) {
        super(CMD.CMD_GET_REWARD_INVITE_CODE, errorCode);
    }
}
