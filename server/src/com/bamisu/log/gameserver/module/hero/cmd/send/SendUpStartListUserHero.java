package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.gamelib.item.entities.EquipDataVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendUpStartListUserHero extends BaseMsg {

    public List<ResourcePackage> listResource;
    public List<EquipDataVO> listEquipData;

    public SendUpStartListUserHero() {
        super(CMD.CMD_UP_STAR_LIST_USER_HERO);
    }

    public SendUpStartListUserHero(short errorCode) {
        super(CMD.CMD_UP_STAR_LIST_USER_HERO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        //MONEY
        for(ResourcePackage money : listResource){
            objPack = new SFSObject();
            objPack.putUtfString(Params.ID, money.id);
            objPack.putInt(Params.AMOUNT, money.amount);

            arrayPack.addSFSObject(objPack);
        }
        //ITEM
        for(EquipDataVO item : listEquipData){
            objPack = new SFSObject();
            objPack.putUtfString(Params.HASH, item.hash);
            objPack.putUtfString(Params.ID, item.id);
            objPack.putInt(Params.LEVEL, item.level);
            objPack.putInt(Params.STAR, item.star);
            objPack.putInt(Params.COUNT, item.count);

            arrayPack.addSFSObject(objPack);
        }

        data.putSFSArray(Params.LIST, arrayPack);
    }
}
