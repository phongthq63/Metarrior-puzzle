package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.base.data.BaseCmd;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Popeye on 3/1/2018.
 */
public class ReceiveVerifyQPUser extends BaseCmd {


    public String userName = "";
    public String pass = "";
    public String dID = "";

    public ReceiveVerifyQPUser(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        this.userName = this.data.getUtfString(Params.USER_NAME);
        this.pass = this.data.getUtfString(Params.USER_PASSWORD);
        this.dID = this.data.getUtfString(Params.DEVICE_ID);
    }
}
