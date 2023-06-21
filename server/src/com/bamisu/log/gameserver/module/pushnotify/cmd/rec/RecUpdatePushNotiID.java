package com.bamisu.log.gameserver.module.pushnotify.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 3:49 PM, 12/28/2020
 */
public class RecUpdatePushNotiID extends BaseCmd {
    public int platform;
    public String id;

    public RecUpdatePushNotiID(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        this.platform = data.getInt(Params.PLATFORM);
        this.id = data.getUtfString(Params.ID);
    }
}
