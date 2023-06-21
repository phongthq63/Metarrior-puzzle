package com.bamisu.log.gameserver.module.hunt.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendLoadSceneHunt extends BaseMsg {

    public SendLoadSceneHunt() {
        super(CMD.CMD_LOAD_SCENE_HUNT);
    }

    public SendLoadSceneHunt(short errorCode) {
        super(CMD.CMD_LOAD_SCENE_HUNT, errorCode);
    }
}
