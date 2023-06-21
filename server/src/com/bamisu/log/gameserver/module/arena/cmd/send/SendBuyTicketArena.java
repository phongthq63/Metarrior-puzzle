package com.bamisu.log.gameserver.module.arena.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendBuyTicketArena extends BaseMsg {

    public SendBuyTicketArena() {
        super(CMD.CMD_BUY_TICKET_ARENA);
    }

    public SendBuyTicketArena(short errorCode) {
        super(CMD.CMD_BUY_TICKET_ARENA, errorCode);
    }
}
