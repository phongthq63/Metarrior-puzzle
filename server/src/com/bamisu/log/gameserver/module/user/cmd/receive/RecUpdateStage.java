package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.Collection;

public class RecUpdateStage extends BaseCmd {
    public String stage;
    public Collection<Integer> stageV2;
    public RecUpdateStage(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        stage = data.getUtfString(Params.STAGE);
        if(data.containsKey(Params.STAGE_V2))
            stageV2 = data.getIntArray(Params.STAGE_V2);
        else
            stageV2 = new ArrayList<>();
    }
}
