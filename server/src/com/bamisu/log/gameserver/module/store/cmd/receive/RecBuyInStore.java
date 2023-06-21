package com.bamisu.log.gameserver.module.store.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecBuyInStore extends BaseCmd {

    public int idStore;
    public int slot;


    public RecBuyInStore(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        idStore = data.getInt(Params.ID_STONE);
        slot = data.getInt(Params.SLOT);
    }
}
