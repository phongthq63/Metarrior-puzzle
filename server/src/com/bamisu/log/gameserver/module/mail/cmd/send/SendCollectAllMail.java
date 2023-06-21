package com.bamisu.log.gameserver.module.mail.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendCollectAllMail extends BaseMsg {
    public SendCollectAllMail() {
        super(CMD.CMD_COLLECT_ALL_MAIL);
    }

    public SendCollectAllMail(short errorCode) {
        super(CMD.CMD_COLLECT_ALL_MAIL, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
    }
}
