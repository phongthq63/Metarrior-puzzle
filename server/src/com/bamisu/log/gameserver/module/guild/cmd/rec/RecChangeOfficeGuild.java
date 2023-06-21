package com.bamisu.log.gameserver.module.guild.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecChangeOfficeGuild extends BaseCmd {

    public String id;
    public long param;
    public long select;

    public RecChangeOfficeGuild(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        id = data.getUtfString(Params.ID);
        param = data.getLong(Params.PARAM);
        select = data.getLong(Params.ModuleChracter.CURRENT);
    }
}
