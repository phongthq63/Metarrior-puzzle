package com.bamisu.log.gameserver.module.IAPBuy.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendClaimIAPPackage extends BaseMsg {

    public String productId;
    public int timeRefresh;

    public SendClaimIAPPackage() {
        super(CMD.CMD_CLAIM_IAP_PACKAGE_ITEM);
    }

    public SendClaimIAPPackage(short errorCode) {
        super(CMD.CMD_CLAIM_IAP_PACKAGE_ITEM, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.PRODUCT_ID, productId);
        data.putInt(Params.TIME_RESET, timeRefresh);
    }
}
