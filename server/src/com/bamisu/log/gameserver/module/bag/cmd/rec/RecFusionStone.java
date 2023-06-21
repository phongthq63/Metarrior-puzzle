package com.bamisu.log.gameserver.module.bag.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.bag.entities.ItemGet;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecFusionStone extends BaseCmd {

    public ItemGet fusion;

    public RecFusionStone(ISFSObject data) {
        super(data);
        unpackData();
    }


    @Override
    public void unpackData() {

        fusion = ItemGet.create(data.getUtfString(Params.HASH), 1);
    }
}
