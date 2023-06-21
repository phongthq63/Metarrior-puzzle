package com.bamisu.log.gameserver.module.vip.cmd.send;

import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.EVip;

public class SendInfoVip extends BaseMsg {
    public SendInfoVip(int cmdId) {
        super(cmdId);
    }

    public SendInfoVip(int cmdId, short errorCode) {
        super(cmdId, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
//        send.time = VipManager.getInstance().getVip(EVip.PROTECTOR, userModel.accountID).expired;
    }
}
