package com.bamisu.log.gameserver.module.arena.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendFightArena extends BaseMsg {

    public SendFightArena() {
        super(CMD.CMD_FIGHT_ARENA);
    }

    public SendFightArena(short errorCode) {
        super(CMD.CMD_FIGHT_ARENA, errorCode);
    }
}
