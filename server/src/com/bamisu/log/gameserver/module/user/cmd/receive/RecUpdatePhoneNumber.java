package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecUpdatePhoneNumber extends BaseCmd {
    public String phone;

    public RecUpdatePhoneNumber(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        this.phone = data.getUtfString(Params.USER_PHONE);
    }
}
