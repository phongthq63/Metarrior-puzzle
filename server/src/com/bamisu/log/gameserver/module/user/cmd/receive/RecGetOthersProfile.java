package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 2:07 PM, 5/8/2020
 */
public class RecGetOthersProfile extends BaseCmd {
    public long uid;

    public RecGetOthersProfile(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        this.uid = data.getLong(Params.UID);
    }
}
