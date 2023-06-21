package com.bamisu.log.gameserver.module.guild.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecGetGuildInfo extends BaseCmd {

    public long gid;

    public RecGetGuildInfo(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        gid = data.getLong(Params.ID);
    }
}
