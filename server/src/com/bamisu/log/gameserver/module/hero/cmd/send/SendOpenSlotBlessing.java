package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.log.gameserver.datamodel.hero.UserBlessingHeroModel;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

public class SendOpenSlotBlessing extends BaseMsg {

    public UserBlessingHeroModel userBlessingHeroModel;

    public SendOpenSlotBlessing() {
        super(CMD.CMD_OPEN_SLOT_BLESSING);
    }

    public SendOpenSlotBlessing(short errorCode) {
        super(CMD.CMD_OPEN_SLOT_BLESSING, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putInt(MoneyType.MIRAGE_ESSENCE.getId(), userBlessingHeroModel.unlockEssence);
        data.putInt(MoneyType.DIAMOND.getId(), userBlessingHeroModel.unlockDiamont);
    }
}
