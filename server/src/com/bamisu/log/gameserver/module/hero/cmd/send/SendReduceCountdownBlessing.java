package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendReduceCountdownBlessing extends BaseMsg {

    public short position;

    public SendReduceCountdownBlessing() {
        super(CMD.CMD_REDUCE_COUNTDOWN_BLESSING);
    }

    public SendReduceCountdownBlessing(short errorCode) {
        super(CMD.CMD_REDUCE_COUNTDOWN_BLESSING, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putShort(Params.POSITION, position);
    }
}
