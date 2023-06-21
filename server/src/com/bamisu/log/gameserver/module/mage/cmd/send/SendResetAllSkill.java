package com.bamisu.log.gameserver.module.mage.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

/**
 * Create by Popeye on 5:19 PM, 3/10/2020
 */
public class SendResetAllSkill extends BaseMsg {
    public SendResetAllSkill() {
        super(CMD.CMD_RESET_ALL_SKILL);
    }

    public SendResetAllSkill(short errorCode) {
        super(CMD.CMD_RESET_ALL_SKILL, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
    }
}
