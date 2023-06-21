package com.bamisu.log.gameserver.module.vip.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.VipData;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendShowInfoVipIAP extends BaseMsg {
    public List<VipData> list;
    public SendShowInfoVipIAP() {
        super(CMD.CMD_SHOW_INFO_VIP_IAP);
    }

    public SendShowInfoVipIAP(short errorCode) {
        super(CMD.CMD_SHOW_INFO_VIP_IAP, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        SFSArray array = new SFSArray();
        for (VipData vipData: list){
            SFSObject sfsObject = new SFSObject();
            sfsObject.putInt(Params.VIP, vipData.eVip.getId());
            sfsObject.putInt(Params.TIME, vipData.expired);
            array.addSFSObject(sfsObject);
        }
        data.putSFSArray(Params.LIST, array);
    }
}
