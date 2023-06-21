package com.bamisu.log.gameserver.module.lucky_draw.cmd.rev;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecLuckyDrawItemsConfig extends BaseCmd {

    public int typeSpin;

    public RecLuckyDrawItemsConfig(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        typeSpin = data.getInt("typeSpin");
    }
}