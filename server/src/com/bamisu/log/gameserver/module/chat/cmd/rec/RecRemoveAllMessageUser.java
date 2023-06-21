package com.bamisu.log.gameserver.module.chat.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecRemoveAllMessageUser extends BaseCmd {

    public long uid;

    public RecRemoveAllMessageUser(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        uid = data.getLong(Params.UID);
    }
}
