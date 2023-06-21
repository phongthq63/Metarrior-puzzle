package com.bamisu.log.gameserver.module.WoL.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecWoLGetRank extends BaseCmd {
//    public int id;
    public RecWoLGetRank(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
//        id = data.getInt(Params.ID);
    }
}
