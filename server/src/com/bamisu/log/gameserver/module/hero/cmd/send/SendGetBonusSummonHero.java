package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;

public class SendGetBonusSummonHero extends BaseMsg {

    public int point;
    public String idCurrentChesst;
    public ISFSArray bonus;

    public SendGetBonusSummonHero() {
        super(CMD.CMD_GET_BONUS_SUMMON_HERO);
    }

    public SendGetBonusSummonHero(short errorCode) {
        super(CMD.CMD_GET_BONUS_SUMMON_HERO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.ModuleChracter.CHEST, idCurrentChesst);
        data.putInt(Params.ModuleChracter.POINT, point);
        data.putSFSArray(Params.ModuleChracter.BONUS, bonus);
    }
}
