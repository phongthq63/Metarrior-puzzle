package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 2:47 PM, 4/21/2020
 */
public class RecChangeServer extends BaseCmd {
    public int serverID;

    public RecChangeServer(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        serverID = data.getInt(Params.SERVER_ID);
    }
}
