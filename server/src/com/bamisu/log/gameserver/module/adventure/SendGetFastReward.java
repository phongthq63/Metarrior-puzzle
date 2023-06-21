package com.bamisu.log.gameserver.module.adventure;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetFastReward extends BaseMsg {
    public List<ResourcePackage> list;
    public SendGetFastReward() {
        super(CMD.CMD_GET_FAST_REWARD);
    }

    public SendGetFastReward(short errorCode) {
        super(CMD.CMD_GET_FAST_REWARD, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        SFSArray sfsArray = new SFSArray();
        for (ResourcePackage vo: list){
//            SFSObject sfsObject = new SFSObject();
//            sfsObject.putUtfString(Params.REPRESENT, String.valueOf(vo.id.charAt(0)));
//            sfsObject.putUtfString(Params.ID, vo.id);
//            sfsObject.putInt(Params.AMOUNT, vo.amount);
//            sfsArray.addSFSObject(sfsObject);
            sfsArray.addSFSObject(vo.toSFSObject());
        }
        data.putSFSArray(Params.LIST, sfsArray);

    }
}
