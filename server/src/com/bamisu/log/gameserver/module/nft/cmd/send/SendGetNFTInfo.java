package com.bamisu.log.gameserver.module.nft.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.TokenResourcePackage;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Quach Thanh Phong
 * On 2/14/2022 - 8:48 PM
 */
public class SendGetNFTInfo extends BaseMsg {

    public Map<String, Object> mapToken;
    public long countHeroMint;


    public SendGetNFTInfo() {
        super(CMD.CMD_GET_NFT_INFO);
    }

    public SendGetNFTInfo(short errorCode) {
        super(CMD.CMD_GET_NFT_INFO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();

        data.putLong(Params.HERO, countHeroMint);

        ISFSArray arrayPack = new SFSArray();
        SFSObject objPack;

        for (String name : mapToken.keySet()) {
            objPack = new SFSObject();
            TokenResourcePackage.putSFSObjectData(objPack, Params.NAME, name);
            TokenResourcePackage.putSFSObjectData(objPack, Params.COUNT, this.mapToken.get(name));
            arrayPack.addSFSObject(objPack);
        }

        data.putSFSArray(Params.TOKEN, arrayPack);
    }
}
