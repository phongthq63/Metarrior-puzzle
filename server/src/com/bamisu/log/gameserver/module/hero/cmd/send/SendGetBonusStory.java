package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendGetBonusStory extends BaseMsg {

    public SendGetBonusStory() {
        super(CMD.CMD_GET_BONUS_STORY);
    }

    public SendGetBonusStory(short errorCode) {
        super(CMD.CMD_GET_BONUS_STORY, errorCode);
    }
}
