package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.item.entities.MoneyPackageVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by Popeye on 9:54 AM, 2/6/2020
 */
public class SendGetAllMoney extends BaseMsg {
    public Map<String, MoneyPackageVO> mapMoney = new HashMap<>();

    public SendGetAllMoney() {
        super(CMD.CMD_GET_ALL_MONEY);
    }

    public SendGetAllMoney(short errorCode) {
        super(CMD.CMD_GET_ALL_MONEY, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        SFSArray arrayMoney = new SFSArray();
        for(MoneyPackageVO vo : mapMoney.values()){
            arrayMoney.addSFSObject(vo.toSFSObject());
        }

        data.putSFSArray(Params.LIST, arrayMoney);
    }
}
