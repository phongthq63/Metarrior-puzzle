package com.bamisu.log.gameserver.module.invite.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendInputInviteCode extends BaseMsg {

    public SendInputInviteCode() {
        super(CMD.CMD_INPUT_INVITE_CODE);
    }

    public SendInputInviteCode(short errorCode) {
        super(CMD.CMD_INPUT_INVITE_CODE, errorCode);
    }
}
