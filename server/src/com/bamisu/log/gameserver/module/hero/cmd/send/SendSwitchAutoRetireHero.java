package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendSwitchAutoRetireHero extends BaseMsg {

    public SendSwitchAutoRetireHero() {
        super(CMD.CMD_SWITCH_AUTO_RETIRE_HERO);
    }

    public SendSwitchAutoRetireHero(short errorCode) {
        super(CMD.CMD_SWITCH_AUTO_RETIRE_HERO, errorCode);
    }
}
