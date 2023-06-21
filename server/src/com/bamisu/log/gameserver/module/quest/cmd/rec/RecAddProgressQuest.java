package com.bamisu.log.gameserver.module.quest.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecAddProgressQuest extends BaseCmd {

    public String id;
    public int count;

    public RecAddProgressQuest(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        id = data.getUtfString(Params.ID);
        count = 1;
    }
}
