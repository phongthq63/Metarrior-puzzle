package com.bamisu.log.gameserver.module.chat.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecSendMessage extends BaseCmd {

    public long to;
    public String message;
    public int type;

    public RecSendMessage(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        to = data.getLong(Params.UID);
        message = data.getUtfString(Params.MESS);
        type = data.getInt(Params.TYPE);
    }
}
