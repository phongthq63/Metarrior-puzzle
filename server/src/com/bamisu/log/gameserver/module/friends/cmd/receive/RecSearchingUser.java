package com.bamisu.log.gameserver.module.friends.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecSearchingUser extends BaseCmd {
    public String key;
    public RecSearchingUser(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        key = data.getUtfString(Params.KEYWORD);
    }
}
