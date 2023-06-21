package com.bamisu.log.gameserver.module.mail.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecCollectMail extends BaseCmd {
    public String idMail;
    public RecCollectMail(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        idMail = data.getUtfString(Params.ID);
    }
}
