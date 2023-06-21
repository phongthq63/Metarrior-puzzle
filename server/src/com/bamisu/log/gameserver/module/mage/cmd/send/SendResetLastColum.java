package com.bamisu.log.gameserver.module.mage.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

/**
 * Create by Popeye on 5:17 PM, 3/10/2020
 */
public class SendResetLastColum extends BaseMsg {
    public SendResetLastColum() {
        super(CMD.CMD_RESET_LAST_COLUM_SKILL);
    }

    public SendResetLastColum(short errorCode) {
        super(CMD.CMD_RESET_LAST_COLUM_SKILL, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
    }
}
