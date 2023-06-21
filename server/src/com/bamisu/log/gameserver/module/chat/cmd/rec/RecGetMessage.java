package com.bamisu.log.gameserver.module.chat.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecGetMessage extends BaseCmd {

    public long uid;
    public int type;
    public int position;

    public RecGetMessage(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        uid = data.getLong(Params.UID);
        type = data.getInt(Params.TYPE);
        position = data.getInt(Params.POSITION);
    }
}
