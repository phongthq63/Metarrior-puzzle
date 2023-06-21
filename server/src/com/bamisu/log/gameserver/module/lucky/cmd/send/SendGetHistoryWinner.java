package com.bamisu.log.gameserver.module.lucky.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.datamodel.lucky.entities.LuckyWinner;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetHistoryWinner extends BaseMsg {

    public List<LuckyWinner> listLuckyWinners;

    public SendGetHistoryWinner() {
        super(CMD.CMD_HIST_WINNER);
    }

    public SendGetHistoryWinner(short errorCode) {super(CMD.CMD_HIST_WINNER, errorCode);;}

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        for(LuckyWinner data : listLuckyWinners){
            objPack = new SFSObject();
            objPack.putUtfString("name",data.name);
            objPack.putLong("user_id", data.userId);
            objPack.putUtfString("is_win", data.isWin);
            objPack.putInt("price_amt", data.priceAmt);
            objPack.putUtfString("create_date", String.valueOf(data.createDate));
            objPack.putInt("total_price", data.totalPrice);
            objPack.putUtfString("type", data.type);

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.LIST, arrayPack);
    }
}
