package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecReduceCountdownBlessing extends BaseCmd {

    public int position;

    public RecReduceCountdownBlessing(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        position = data.getShort(Params.POSITION);
    }
}
