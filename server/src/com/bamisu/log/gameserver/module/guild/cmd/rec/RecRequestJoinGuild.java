package com.bamisu.log.gameserver.module.guild.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecRequestJoinGuild extends BaseCmd {

    public long gid;

    public RecRequestJoinGuild(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        gid = data.getLong(Params.ID);
    }
}
