package com.bamisu.log.gameserver.module.guild.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendRemoveAllGiftGuild extends BaseMsg {

    public SendRemoveAllGiftGuild() {
        super(CMD.CMD_REMOVE_ALL_GIFT_GUILD);
    }

    public SendRemoveAllGiftGuild(short errorCode) {
        super(CMD.CMD_REMOVE_ALL_GIFT_GUILD, errorCode);
    }
}
