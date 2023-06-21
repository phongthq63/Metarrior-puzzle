package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecLinkUsername extends BaseCmd {
    public String username;
    public String password;
    public String email;
    public String code;

    public RecLinkUsername(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        this.username = this.data.getText(Params.USER_NAME);
        this.password = this.data.getText(Params.USER_PASSWORD);
        this.email = this.data.getText(Params.USER_EMAIL);
        this.code = this.data.getText(Params.CODE);
    }
}
