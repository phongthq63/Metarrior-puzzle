package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecConnectFB extends BaseCmd {
    public String tk;

    public RecConnectFB(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        tk = data.getUtfString(Params.TOKEN);
    }
}
