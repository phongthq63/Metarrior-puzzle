package com.bamisu.log.gameserver.module.mail.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendCollectMail extends BaseMsg {
    public SendCollectMail() {
        super(CMD.CMD_CONFIRM_MAIL);
    }

    public SendCollectMail(short errorCode) {
        super(CMD.CMD_CONFIRM_MAIL, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
