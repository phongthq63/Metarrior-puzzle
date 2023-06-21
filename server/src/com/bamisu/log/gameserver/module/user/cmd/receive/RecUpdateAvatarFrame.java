package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 1:48 PM, 4/15/2020
 */
public class RecUpdateAvatarFrame extends BaseCmd {
    public int id;

    public RecUpdateAvatarFrame(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        this.id = data.getInt(Params.ID);
    }
}
