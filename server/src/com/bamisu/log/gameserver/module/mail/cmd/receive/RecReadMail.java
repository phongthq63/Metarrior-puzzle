package com.bamisu.log.gameserver.module.mail.cmd.receive;


import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecReadMail extends BaseCmd {
    public String id;
    public RecReadMail(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        id = data.getUtfString(Params.ID);
    }
}
