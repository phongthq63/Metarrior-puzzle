package com.bamisu.log.gameserver.module.quest.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendAddProgressQuest extends BaseMsg {

    public SendAddProgressQuest() {
        super(CMD.CMD_ADD_PROGRESS_QUEST);
    }

    public SendAddProgressQuest(short errorCode) {
        super(CMD.CMD_ADD_PROGRESS_QUEST, errorCode);
    }
}
