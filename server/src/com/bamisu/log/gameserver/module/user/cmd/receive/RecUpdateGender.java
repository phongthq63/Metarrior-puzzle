package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 5:29 PM, 4/8/2020
 */
public class RecUpdateGender extends BaseCmd {
    public short gender;

    public RecUpdateGender(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        this.gender = data.getShort(Params.GENDER);
    }
}
