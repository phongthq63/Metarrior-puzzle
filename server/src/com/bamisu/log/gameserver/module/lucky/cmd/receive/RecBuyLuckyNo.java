package com.bamisu.log.gameserver.module.lucky.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecBuyLuckyNo extends BaseCmd {
    public String type;
    public String id;
    public int no1;
    public int no2;
    public int no3;
    public int amount;


    public RecBuyLuckyNo(ISFSObject data) {
        super(data);
        unpackData();
    }


    @Override
    public void unpackData() {
        id = data.getUtfString(Params.ID);
        no1 = data.getInt(Params.NO1_LUCKY);
        no2 = data.getInt(Params.NO2_LUCKY);
        no3 = data.getInt(Params.NO3_LUCKY);
        type = data.getUtfString(Params.TYPE_LUCKY);
        amount = data.getInt(Params.AMOUNT_LUCKY);
    }
}
