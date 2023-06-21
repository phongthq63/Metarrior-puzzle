package com.bamisu.log.gameserver.module.adventure.cmd.send;

import com.bamisu.log.gameserver.datamodel.bag.entities.FastRewardDataVO;
import com.bamisu.log.gameserver.module.adventure.entities.FastRewardVO;
import com.bamisu.gamelib.item.entities.MoneyPackageVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class SendClickOnFastReward extends BaseMsg {
    public FastRewardDataVO reward;
    public int timeReset;
    public SendClickOnFastReward() {
        super(CMD.CMD_CLICK_ON_FAST_REWARD);
    }

    public SendClickOnFastReward(short errorCode) {
        super(CMD.CMD_CLICK_ON_FAST_REWARD, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        data.putInt(Params.ID, reward.id);
        data.putInt(Params.COUNT, reward.count);
        data.putInt(Params.TIME, timeReset);
    }
}
