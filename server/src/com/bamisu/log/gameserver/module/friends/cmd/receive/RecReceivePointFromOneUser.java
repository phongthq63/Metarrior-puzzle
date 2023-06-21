package com.bamisu.log.gameserver.module.friends.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecReceivePointFromOneUser extends BaseCmd {
    public long uid;
    public RecReceivePointFromOneUser(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        uid = data.getLong(Params.UID);
    }
}
