package com.bamisu.log.gameserver.module.chat.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendLeaveSceneChat extends BaseMsg {

    public SendLeaveSceneChat() {
        super(CMD.CMD_LEAVE_SCENE_CHAT);
    }

    public SendLeaveSceneChat(short errorCode) {
        super(CMD.CMD_LEAVE_SCENE_CHAT, errorCode);
    }
}
