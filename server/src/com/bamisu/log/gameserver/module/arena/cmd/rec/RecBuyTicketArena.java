package com.bamisu.log.gameserver.module.arena.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecBuyTicketArena extends BaseCmd {

    public short count;

    public RecBuyTicketArena(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        count = data.getShort(Params.COUNT);
    }
}
