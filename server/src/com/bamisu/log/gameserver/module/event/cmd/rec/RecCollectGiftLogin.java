package com.bamisu.log.gameserver.module.event.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecCollectGiftLogin extends BaseCmd {
    public int day;
    public String kingdom = "";
    public RecCollectGiftLogin(ISFSObject data) {
        super(data);
        this.unpackData();
    }

    @Override
    public void unpackData() {
        this.day = this.data.getInt(Params.DAY);
        if (this.data.containsKey(Params.KINGDOM)) {
            this.kingdom = this.data.getText(Params.KINGDOM);
        }
    }
}
