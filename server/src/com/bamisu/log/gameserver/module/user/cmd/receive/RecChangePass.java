package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Popeye on 6/21/2017.
 */
public class RecChangePass extends BaseCmd {
    public String oldPass = "";
    public String newPass = "";
    public RecChangePass(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        oldPass = data.getUtfString(Params.USER_OLD_PASS);
        newPass = data.getUtfString(Params.USER_PASSWORD);
    }
}
