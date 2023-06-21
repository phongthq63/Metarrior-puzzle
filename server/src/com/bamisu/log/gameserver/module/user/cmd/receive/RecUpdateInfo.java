package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Popeye on 6/22/2017.
 */
public class RecUpdateInfo extends BaseCmd {
    public String email = "";
    public short sex = -1;

    public RecUpdateInfo(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        email = getTextParam(Params.USER_EMAIL);
        sex = getShortgParam(Params.USER_SEX);
    }

    private String getTextParam(String key){
        if(data.containsKey(key)) return data.getUtfString(key);
        return "";
    }

    private Short getShortgParam(String key){
        if(data.containsKey(key)) return data.getShort(key);
        return -1;
    }
}
