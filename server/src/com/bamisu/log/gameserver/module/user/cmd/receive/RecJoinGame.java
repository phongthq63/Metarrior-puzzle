package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Popeye on 5/25/2017.
 */
public class RecJoinGame extends BaseCmd {
    public int gameID;
//    public int typeID;
//    public int betLevel;
    public RecJoinGame(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        this.gameID = data.getInt(Params.gameID);
//        this.typeID = data.getInt("type_id");
//        this.betLevel = data.getInt("bet_level");
    }
}
