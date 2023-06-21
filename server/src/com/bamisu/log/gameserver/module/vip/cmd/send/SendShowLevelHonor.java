package com.bamisu.log.gameserver.module.vip.cmd.send;

import com.bamisu.log.gameserver.module.vip.entities.HonorDataVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendShowLevelHonor extends BaseMsg {
    public List<HonorDataVO> list;
    public SendShowLevelHonor() {
        super(CMD.CMD_SHOW_LIST_HONOR);
    }

    public SendShowLevelHonor(short errorCode) {
        super(CMD.CMD_SHOW_LIST_HONOR, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        SFSArray sfsArray = new SFSArray();
        for (HonorDataVO vo: list){
            SFSObject sfsObject = new SFSObject();
            sfsObject.putInt(Params.ID, vo.id);
            sfsObject.putInt(Params.STATUS, vo.status);
            sfsArray.addSFSObject(sfsObject);
        }
        data.putSFSArray(Params.LIST, sfsArray);
    }
}
