package com.bamisu.log.gameserver.module.guild.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecGetListGuildInfo extends BaseCmd {

    public int position;
    public String nameOrId;

    public RecGetListGuildInfo(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
//        position = data.getInt(Params.POSITION);
        nameOrId = data.getUtfString(Params.ID);
    }
}
