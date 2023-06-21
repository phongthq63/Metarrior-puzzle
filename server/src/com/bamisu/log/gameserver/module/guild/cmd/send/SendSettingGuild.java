package com.bamisu.log.gameserver.module.guild.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendSettingGuild extends BaseMsg {

    public String type = "";

    public SendSettingGuild() {
        super(CMD.CMD_SETTING_GUILD);
    }

    public SendSettingGuild(short errorCode) {
        super(CMD.CMD_SETTING_GUILD, errorCode);

    }

    @Override
    public void packData() {
        super.packData();

        data.putUtfString(Params.TYPE, type);
    }
}
