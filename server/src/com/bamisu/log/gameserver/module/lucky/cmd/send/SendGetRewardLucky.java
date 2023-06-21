package com.bamisu.log.gameserver.module.lucky.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class SendGetRewardLucky extends BaseMsg {

    public int price;
    public String type;

    public SendGetRewardLucky() {
        super(CMD.CMD_REWARD_LUCKY);
    }

    public SendGetRewardLucky(short errorCode) {
        super(CMD.CMD_REWARD_LUCKY, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;
        ISFSObject object = new SFSObject();
        object.putInt("price_amt", price);
        object.putUtfString("type", type);
        data.putSFSObject(Params.REWARD, object);
    }
}
