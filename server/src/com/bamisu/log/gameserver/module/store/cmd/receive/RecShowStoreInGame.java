package com.bamisu.log.gameserver.module.store.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecShowStoreInGame extends BaseCmd {
    public int idStore;
    public RecShowStoreInGame(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        idStore = data.getInt(Params.ID_STONE);
    }
}
