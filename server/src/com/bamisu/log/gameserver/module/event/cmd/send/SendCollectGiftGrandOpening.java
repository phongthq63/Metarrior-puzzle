package com.bamisu.log.gameserver.module.event.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;

public class SendCollectGiftGrandOpening extends BaseMsg {
//    public ResourcePackage resourcePackage;
    public SendCollectGiftGrandOpening() {
        super(CMD.CMD_COLLECT_GIFT_GRAND_OPENING);
    }

    public SendCollectGiftGrandOpening(short errorCode) {
        super(CMD.CMD_COLLECT_GIFT_GRAND_OPENING, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
//        data.putUtfString(Params.ID, resourcePackage.id);
//        data.putInt(Params.AMOUNT, resourcePackage.amount);
    }
}
