package com.bamisu.log.gameserver.module.guild.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendRemoveGiftGuild extends BaseMsg {

    public SendRemoveGiftGuild() {
        super(CMD.CMD_REMOVE_GIFT_GUILD);
    }

    public SendRemoveGiftGuild(short errorCode) {
        super(CMD.CMD_REMOVE_GIFT_GUILD, errorCode);
    }
}
