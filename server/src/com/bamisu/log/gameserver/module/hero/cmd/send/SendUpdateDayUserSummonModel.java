package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendUpdateDayUserSummonModel extends BaseMsg {

    public SendUpdateDayUserSummonModel() {
        super(CMD.CMD_UPDATE_DAY_SUMMON_HERO);
    }

    public SendUpdateDayUserSummonModel(short errorCode) {
        super(CMD.CMD_UPDATE_DAY_SUMMON_HERO, errorCode);
    }
}
