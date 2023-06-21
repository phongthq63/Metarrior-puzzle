package com.bamisu.log.gameserver.module.adventure.cmd.send;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendLootItem extends BaseMsg {
    public List<ResourcePackage> listResource;
    public SendLootItem() {
        super(CMD.CMD_LOOT_ITEM);
    }

    public SendLootItem(short errorCode) {
        super(CMD.CMD_LOOT_ITEM, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        SFSArray sfsArray = new SFSArray();
        if (listResource != null){
            for (ResourcePackage vo: listResource){
                if (vo.amount != 0){
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString(Params.ID, vo.id);
                    sfsObject.putLong(Params.AMOUNT, vo.amount);
                    sfsArray.addSFSObject(sfsObject);
                }
            }
            data.putSFSArray(Params.LIST, sfsArray);
        }
    }
}
