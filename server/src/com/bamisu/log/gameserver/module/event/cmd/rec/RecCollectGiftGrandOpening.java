package com.bamisu.log.gameserver.module.event.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecCollectGiftGrandOpening extends BaseCmd {
    public int position;
    public RecCollectGiftGrandOpening(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        position = data.getInt(Params.POSITION);
    }
}
