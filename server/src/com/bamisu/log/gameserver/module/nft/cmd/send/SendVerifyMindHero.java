package com.bamisu.log.gameserver.module.nft.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.nft.entities.HeroToken;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

/**
 * Created by Quach Thanh Phong
 * On 2/13/2022 - 10:34 AM
 */
public class SendVerifyMindHero extends BaseMsg {

    public List<HeroToken> listHeroToken;

    public SendVerifyMindHero() {
        super(CMD.CMD_VERIFY_MINT_HERO);
    }

    public SendVerifyMindHero(short errorCode) {
        super(CMD.CMD_VERIFY_MINT_HERO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        for (HeroToken heroToken : listHeroToken) {
            objPack = new SFSObject();
            objPack.putUtfString(Params.HASH, heroToken.hashHero);
            objPack.putUtfString(Params.TOKEN, heroToken.tokenId);

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.LIST,  arrayPack);
    }
}
