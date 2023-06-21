package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecOpenSlotBlessing extends BaseCmd {

    public String idMoney;

    public RecOpenSlotBlessing(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        idMoney = data.getUtfString(Params.ID);
    }
}
