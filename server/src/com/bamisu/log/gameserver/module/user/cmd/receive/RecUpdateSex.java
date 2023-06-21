package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecUpdateSex extends BaseCmd {
    public short sex;
    public RecUpdateSex(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        sex = data.getShort(Params.USER_SEX);
    }
}
