package com.bamisu.log.gameserver.module.mail.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecDeleteAllMail extends BaseCmd {
    public RecDeleteAllMail(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {

    }
}
