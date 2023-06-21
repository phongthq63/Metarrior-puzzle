package com.bamisu.log.gameserver.module.invite.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecInputInviteCode extends BaseCmd {

    public String code;

    public RecInputInviteCode(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        code = data.getUtfString(Params.CODE).toUpperCase();
    }
}
