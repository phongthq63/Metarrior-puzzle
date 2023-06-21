package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Popeye on 4/15/2017.
 */
public class RecLogin extends BaseCmd {
    public String token;
    public String password;
    public int loginType;
    public int os;
    public String did;
    public byte action; //0 login, 1 reconnect

    public RecLogin(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        this.token = data.getUtfString(Params.TOKEN);
        this.loginType = data.getInt(Params.USER_LOGIN_TYPE);
        this.os = data.containsKey(Params.OS) ? data.getInt(Params.OS) : 0;
        this.did = data.containsKey(Params.DID) ? data.getUtfString(Params.DID) : "";
        this.password = data.containsKey(Params.USER_PASSWORD) ? data.getUtfString(Params.USER_PASSWORD) : "";

        this.action = 0;
        if(data.containsKey(Params.ACTION)){
            this.action = data.getByte(Params.ACTION);
        }
    }
}
