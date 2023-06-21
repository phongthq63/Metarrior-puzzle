package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.item.entities.StoneDataVO;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendUpLevelWeapon extends BaseMsg {

    public List<ResourcePackage> listHammer;
    public List<StoneDataVO> listStoneData;


    public SendUpLevelWeapon() {
        super(CMD.CMD_UP_LEVEL_WEAPON);
    }

    public SendUpLevelWeapon(short errorCode) {
        super(CMD.CMD_UP_LEVEL_WEAPON, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        //Hammer add
        ISFSArray arrayPack = new SFSArray();
        SFSObject objPack;
        for (ResourcePackage hammer: listHammer){
            objPack = new SFSObject();

            objPack.putUtfString(Params.ID, hammer.id);
            objPack.putInt(Params.AMOUNT, hammer.amount);
            arrayPack.addSFSObject(objPack);
        }
        //Stone add vao bag
        for(StoneDataVO stone : listStoneData){
            objPack = new SFSObject();

            objPack.putUtfString(Params.HASH, stone.hash);
            objPack.putUtfString(Params.ID, stone.id);
            objPack.putInt(Params.AMOUNT, stone.count);
            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.LIST, arrayPack);
    }
}
