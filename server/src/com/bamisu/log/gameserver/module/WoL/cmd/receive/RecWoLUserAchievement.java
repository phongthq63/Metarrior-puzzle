package com.bamisu.log.gameserver.module.WoL.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecWoLUserAchievement extends BaseCmd {
    public int area;
    public int stage;
    public RecWoLUserAchievement(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        area = data.getInt(Params.AREA);
        stage = data.getInt(Params.STAGE);
    }
}
