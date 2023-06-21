package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

/**
 * Create by Popeye on 11:57 AM, 4/14/2020
 */
public class SendUpdateLanguage extends BaseMsg {
    public String languageID;

    public SendUpdateLanguage() {
        super(CMD.CMD_UPDATE_LANGUGE);
    }

    public SendUpdateLanguage(short errorCode) {
        super(CMD.CMD_UPDATE_LANGUGE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putUtfString(Params.LANGUAGE, languageID);
    }
}
