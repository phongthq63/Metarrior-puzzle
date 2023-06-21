package com.bamisu.log.gameserver.module.nft.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Map;

/**
 * Created by Quach Thanh Phong
 * On 2/16/2022 - 12:02 AM
 */
public class SendGetTokenBlockChainInfo extends BaseMsg {

    public Map<String,Long> mapToken;

    public SendGetTokenBlockChainInfo() {
        super(CMD.CMD_GET_TOKEN_BLOCKCHAIN_INFO);
    }

    @Override
    public void packData() {
        super.packData();

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        for (String name : mapToken.keySet()) {
            objPack = new SFSObject();
            objPack.putUtfString(Params.NAME, name);
            objPack.putLong(Params.COUNT, mapToken.get(name));

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.LIST, arrayPack);
    }
}
