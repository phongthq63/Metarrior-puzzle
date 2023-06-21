package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Popeye on 5/3/2017.
 */
public class RecQuickPlay extends BaseCmd {
    public String dID;
    public int os;
    public String osV;
    public RecQuickPlay(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        this.dID = this.data.getUtfString(Params.DEVICE_ID);
        this.os = this.data.getInt(Params.OS);
        this.osV = this.data.getUtfString(Params.OS_VERSION);
    }
}
