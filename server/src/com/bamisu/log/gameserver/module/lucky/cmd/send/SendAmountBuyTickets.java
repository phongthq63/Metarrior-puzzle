package com.bamisu.log.gameserver.module.lucky.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.log.gameserver.module.lucky.LuckyManager;

public class SendAmountBuyTickets extends BaseMsg {
    public SendAmountBuyTickets(){
        super(CMD.CMD_AMOUNT_TO_BUY_TICKETS);
    }

    public LuckyManager luckyManager;
    @Override
    public void packData(){
        super.packData();
        if(isError())return;
        data.putInt("amount_MEWA",luckyManager.amountMewaOfTicket);
        data.putInt("amount_SOG", luckyManager.amountSogOfTicket);
    }
}
