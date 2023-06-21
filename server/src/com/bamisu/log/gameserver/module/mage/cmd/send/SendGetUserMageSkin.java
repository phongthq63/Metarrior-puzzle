package com.bamisu.log.gameserver.module.mage.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSArray;

import java.util.Set;

public class SendGetUserMageSkin extends BaseMsg {

    public Set<String> listSkin;

    public SendGetUserMageSkin() {
        super(CMD.CMD_GET_USER_MAGE_SKIN);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putSFSArray(Params.LIST, SFSArray.newFromJsonData(Utils.toJson(listSkin)));
    }
}
