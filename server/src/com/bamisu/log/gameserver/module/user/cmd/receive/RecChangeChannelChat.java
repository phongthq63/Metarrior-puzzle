package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecChangeChannelChat extends BaseCmd {

    public String idChannel;

    public RecChangeChannelChat(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        idChannel = data.getUtfString(Params.ID);
    }
}
