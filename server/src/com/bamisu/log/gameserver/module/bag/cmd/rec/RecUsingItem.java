package com.bamisu.log.gameserver.module.bag.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecUsingItem extends BaseCmd {

    public String id;
    public int amount;
    public int position;


    public RecUsingItem(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        id = data.getUtfString(Params.ID);
        amount = data.getInt(Params.AMOUNT);
        position = data.getInt(Params.AMOUNT);
    }
}
