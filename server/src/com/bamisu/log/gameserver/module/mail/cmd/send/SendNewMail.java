package com.bamisu.log.gameserver.module.mail.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendNewMail extends BaseMsg {
//    public String idMail;
    public SendNewMail() {
        super(CMD.CMD_NEW_MAIL);
    }

    public SendNewMail(short errorCode) {
        super(CMD.CMD_NEW_MAIL, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
//        data.putUtfString(Params.ID, idMail);
    }
}
