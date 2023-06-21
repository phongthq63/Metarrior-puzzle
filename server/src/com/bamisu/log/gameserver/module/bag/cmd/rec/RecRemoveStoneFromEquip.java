package com.bamisu.log.gameserver.module.bag.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecRemoveStoneFromEquip extends BaseCmd {

    public String hash;
    public int position;

    public RecRemoveStoneFromEquip(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {

        hash = data.getUtfString(Params.HASH);
        position = data.getInt(Params.POSITION);
    }
}
