package com.bamisu.log.gameserver.module.guild.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSArray;

import java.util.List;

public class SendCheckInGuild extends BaseMsg {

    public List<ResourcePackage> reward;

    public SendCheckInGuild() {
        super(CMD.CMD_CHECK_IN_GUILD);
    }

    public SendCheckInGuild(short errorCode) {
        super(CMD.CMD_CHECK_IN_GUILD, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putSFSArray(Params.REWARD, SFSArray.newFromJsonData(Utils.toJson(reward)));
    }
}
