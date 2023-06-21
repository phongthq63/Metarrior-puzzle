package com.bamisu.log.gameserver.module.quest.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendGetRewardChestQuest extends BaseMsg {

    public String id;
    public byte type;

    public SendGetRewardChestQuest() {
        super(CMD.CMD_GET_REWARD_CHEST_QUEST);
    }

    public SendGetRewardChestQuest(short errorCode) {
        super(CMD.CMD_GET_REWARD_CHEST_QUEST, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        data.putUtfString(Params.ID, id);
        data.putByte(Params.TYPE, type);
    }
}
