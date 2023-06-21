package com.bamisu.log.gameserver.module.guild.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecRemoveGiftGuild extends BaseCmd {

    public String hashGift;

    public RecRemoveGiftGuild(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        hashGift = data.getUtfString(Params.HASH);
    }
}
