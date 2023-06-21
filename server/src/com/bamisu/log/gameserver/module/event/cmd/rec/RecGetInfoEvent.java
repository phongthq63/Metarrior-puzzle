package com.bamisu.log.gameserver.module.event.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecGetInfoEvent extends BaseCmd {

    public String id;

    public RecGetInfoEvent(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        id = data.getUtfString(Params.ID);
    }
}
