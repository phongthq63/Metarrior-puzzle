package com.bamisu.log.gameserver.module.IAPBuy.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecGetInfoIAPTab extends BaseCmd {

    public String id;

    public RecGetInfoIAPTab(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        id = data.getUtfString(Params.ID);
    }
}
