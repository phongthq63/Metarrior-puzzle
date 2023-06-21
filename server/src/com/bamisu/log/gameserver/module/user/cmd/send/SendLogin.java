package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.base.data.BaseMsg;

/**
 * Created by Popeye on 5/3/2017.
 */
public class SendLogin extends BaseMsg {

    public SendLogin() {
        super(CMD.CMD_LOGIN);
    }

    public SendLogin(short errCode) {
        super(CMD.CMD_LOGIN, errCode);
    }
    @Override
    public void packData(){
        super.packData();
    }

}
