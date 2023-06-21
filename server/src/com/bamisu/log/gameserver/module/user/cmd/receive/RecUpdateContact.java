package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecUpdateContact extends BaseCmd {
    public String info;
    public RecUpdateContact(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        info = data.getUtfString(Params.CONTACT_INFO);
    }
}
