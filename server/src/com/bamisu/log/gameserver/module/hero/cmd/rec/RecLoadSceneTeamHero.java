package com.bamisu.log.gameserver.module.hero.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecLoadSceneTeamHero extends BaseCmd {

    public String teamType;
    public long uid;

    public RecLoadSceneTeamHero(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        teamType = data.getUtfString(Params.TYPE);
        uid = data.containsKey(Params.UID) ? data.getLong(Params.UID) : -1;
    }
}
