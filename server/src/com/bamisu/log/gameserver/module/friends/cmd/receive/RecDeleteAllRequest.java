package com.bamisu.log.gameserver.module.friends.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecDeleteAllRequest extends BaseCmd {
    public RecDeleteAllRequest(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {

    }
}
