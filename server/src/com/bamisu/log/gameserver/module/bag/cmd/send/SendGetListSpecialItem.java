package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.item.entities.SpecialItemPackageVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Map;

public class SendGetListSpecialItem extends BaseMsg {

    public Map<String, SpecialItemPackageVO> mapItem = null;


    public SendGetListSpecialItem() {
        super(CMD.CMD_GET_LIST_SPECIAL_ITEM);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        SFSArray arrayPack = new SFSArray();
        SFSObject objectPack;
        for (Map.Entry<String, SpecialItemPackageVO> entry : mapItem.entrySet()){
            if(entry.getValue().amount <= 0) continue;
            objectPack = new SFSObject();

            objectPack.putUtfString(Params.ID, entry.getValue().id);
            objectPack.putInt(Params.AMOUNT, entry.getValue().amount);
            arrayPack.addSFSObject(objectPack);
        }
        data.putSFSArray(Params.LIST, arrayPack);
    }
}
