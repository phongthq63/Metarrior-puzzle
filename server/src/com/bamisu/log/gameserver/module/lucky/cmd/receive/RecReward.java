package com.bamisu.log.gameserver.module.lucky.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.mysql.fabric.xmlrpc.base.Param;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecReward extends BaseCmd {
    public String create_day;
    public String type;

    public RecReward(ISFSObject data){
        super(data);
        unpackData();
    }
    @Override
    public void unpackData() {
        create_day = data.getUtfString("create_day");
        type = data.getUtfString("type");
    }
}
