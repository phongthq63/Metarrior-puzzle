package com.bamisu.log.gameserver.module.guild.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecExecutionRequestJoinGuild extends BaseCmd {

    public long uid;
    public boolean accept;
    public boolean all;

    public RecExecutionRequestJoinGuild(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {

        all = data.getBool(Params.ModuleGuild.ALL);
        accept = data.getBool(Params.ModuleGuild.CHOICE);
        if(!all){
            uid = data.getLong(Params.ID);
        }
    }
}
