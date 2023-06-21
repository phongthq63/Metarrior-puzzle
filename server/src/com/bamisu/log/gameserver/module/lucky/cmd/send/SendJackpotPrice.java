package com.bamisu.log.gameserver.module.lucky.cmd.send;

import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.datamodel.lucky.LuckyModel;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;

public class SendJackpotPrice extends BaseMsg {
    public SendJackpotPrice() {
        super(CMD.CMD_GET_LUCKY_PRICE);
    }
    public LuckyModel jportModel;

    @Override
    public void packData() {
        super.packData();
        if(isError())return;
        data.putInt(Params.MEWA_LUCKY,Integer.valueOf((int) jportModel.mewa));
        data.putInt(Params.SOG_LUCKY,Integer.valueOf((int) jportModel.sog));
        data.putInt(Params.TIME, TimeUtils.getDeltaTimeToTime(ETimeType.NEW_DAY, jportModel.timeStamp));
    }
}
