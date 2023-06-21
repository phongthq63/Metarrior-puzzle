package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Map;

public class SendGetMoney extends BaseMsg {
//    public Map<String, MoneyTypeVO> listMoney = null;
    public SendGetMoney() {
        super(CMD.CMD_GET_MONEY);
    }

    public SendGetMoney(short errorCode) {
        super(CMD.CMD_GET_MONEY, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        SFSArray array = new SFSArray();
//        for (Map.Entry vo: listMoney.entrySet()){
//            SFSObject sfsObject = new SFSObject();
//            sfsObject.putUtfString(Params.ID_MONEY, listMoney.get(vo.getKey()).getId());
//            sfsObject.putUtfString(Params.NAME_MONEY, listMoney.get(vo.getKey()).shortName());
//            sfsObject.putDouble(Params.AMOUNT_MONEY, listMoney.get(vo.getKey()).getAmount());
//            array.addSFSObject(sfsObject);
//        }
//        data.putSFSArray(Params.LIST_MONEY, array);
    }
}
