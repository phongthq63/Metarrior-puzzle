package com.bamisu.log.gameserver.module.mail.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendDeleteAllMail extends BaseMsg {
    public SendDeleteAllMail() {
        super(CMD.CMD_DELETE_ALL_MAIL);
    }

    public SendDeleteAllMail(short errorCode) {
        super(CMD.CMD_DELETE_ALL_MAIL, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
