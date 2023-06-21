package com.bamisu.log.gameserver.module.lucky.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.datamodel.lucky.LuckyNumberModel;
import com.bamisu.log.gameserver.datamodel.lucky.entities.LuckyWinner;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SendGetListHistoryLucky extends BaseMsg {

    public SendGetListHistoryLucky() {
        super(CMD.CMD_HIST_LUCKY);
        this.listHistory = new ArrayList<>();
        this.listLuckyNumberModel = new ArrayList<>();
    }

    public List<LuckyNumberModel> listLuckyNumberModel;
    public List<LuckyWinner> listHistory;

    @Override
    public void packData() {
        super.packData();
        if (isError()) return;
        ISFSObject object = new SFSObject();
        ISFSArray ticketBuyArray = new SFSArray();

        LuckyNumberModel ticket_jackpot = listLuckyNumberModel.get(0) != null ? listLuckyNumberModel.get(0) : (new LuckyNumberModel());
        ISFSObject ticketJackpotOutput = new SFSObject();
        ticketJackpotOutput.putInt(Params.NO1_JACKPOT, ticket_jackpot.num1);
        ticketJackpotOutput.putInt(Params.NO2_JACKPOT, ticket_jackpot.num2);
        ticketJackpotOutput.putInt(Params.NO3_JACKPOT, ticket_jackpot.num3);

        Function<LuckyWinner, ISFSObject> convertLuckArrayOutput = (lucky) -> {
            ISFSObject ticketBuyOutput = new SFSObject();
            ticketBuyOutput.putInt(Params.NO1_LUCKY, lucky.no1);
            ticketBuyOutput.putInt(Params.NO2_LUCKY, lucky.no2);
            ticketBuyOutput.putInt(Params.NO3_LUCKY, lucky.no3);
            ticketBuyOutput.putUtfString(Params.TYPE_LUCKY, lucky.type);
            ticketBuyOutput.putInt(Params.AMOUNT, lucky.amount);
            //ticketBuyOutput.putInt(Params.TIME, Utils.getTimestampInSecond());
            ticketBuyOutput.putUtfString(Params.IS_WIN, lucky.isWin);
            ticketBuyOutput.putUtfString(Params.IS_REV_REWARD, lucky.received == 0 ? "": "1");
            ticketBuyOutput.putInt(Params.PRICE_AMT, Integer.valueOf(lucky.priceAmt));
            return ticketBuyOutput;
        };


        this.listHistory.stream()
                .map(convertLuckArrayOutput)
                .forEach(ticketBuyArray::addSFSObject);


        object.putSFSObject(Params.TICKET_JACKPOT, ticketJackpotOutput);
        object.putUtfString(Params.TIME_DAY, ticket_jackpot.day!=null?ticket_jackpot.day:"");
        object.putSFSArray(Params.TICKET_BUY, ticketBuyArray);

        data.putSFSObject("history_lucky", object);
    }
}
