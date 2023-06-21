package com.bamisu.log.gameserver.module.lucky.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecUserHistoryBuy extends BaseCmd {
    public String date;

    public RecUserHistoryBuy(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        date = data.getUtfString("dayfilter");

    }
}
