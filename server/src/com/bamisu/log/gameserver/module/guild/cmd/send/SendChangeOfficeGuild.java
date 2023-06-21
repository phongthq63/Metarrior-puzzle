package com.bamisu.log.gameserver.module.guild.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.datamodel.guild.GuildModel;

public class SendChangeOfficeGuild extends BaseMsg {

    public GuildModel guildModel;

    public SendChangeOfficeGuild() {
        super(CMD.CMD_CHANGE_OFFICE_GUILD);
    }

    public SendChangeOfficeGuild(short errorCode) {
        super(CMD.CMD_CHANGE_OFFICE_GUILD, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putLong(Params.ModuleGuild.MASTER, guildModel.guildMaster);
        data.putLongArray(Params.ModuleGuild.VICE, guildModel.guildVice);
        data.putLongArray(Params.ModuleGuild.LEAD, guildModel.guildLeader);
    }
}
