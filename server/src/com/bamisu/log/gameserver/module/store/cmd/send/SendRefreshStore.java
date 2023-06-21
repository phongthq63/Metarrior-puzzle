package com.bamisu.log.gameserver.module.store.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendRefreshStore extends BaseMsg {
    public int idStore;
    public SendRefreshStore() {
        super(CMD.CMD_REFRESH_STORE);
    }

    public SendRefreshStore(short errorCode) {
        super(CMD.CMD_REFRESH_STORE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        data.putInt(Params.ID_STONE, idStore);
    }
}
