package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendUpdateTeamHero extends BaseMsg {

    public SendUpdateTeamHero() {
        super(CMD.CMD_UPDATE_TEAM_HERO);
    }

    public SendUpdateTeamHero(short errorCode) {
        super(CMD.CMD_UPDATE_TEAM_HERO, errorCode);
    }
}
