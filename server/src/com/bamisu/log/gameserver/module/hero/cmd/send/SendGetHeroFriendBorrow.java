package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetHeroFriendBorrow extends BaseMsg {

    public List<HeroModel> listHeroModel;
    public Zone zone;

    public SendGetHeroFriendBorrow() {
        super(CMD.CMD_GET_HERO_FRIEND_BORROW);
    }

    public SendGetHeroFriendBorrow(short errorCode) {
        super(CMD.CMD_GET_HERO_FRIEND_BORROW, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        for (HeroModel heroModel : listHeroModel) {
            objPack = new SFSObject();

            objPack.putUtfString(Params.HASH_HERO, heroModel.hash);
            objPack.putUtfString(Params.ID, heroModel.id);
            objPack.putShort(Params.LEVEL, heroModel.readLevel());
            objPack.putShort(Params.STAR, heroModel.star);
            objPack.putInt(Params.POWER, HeroManager.getInstance().getPower(heroModel, zone));
            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.LIST, arrayPack);
    }
}
