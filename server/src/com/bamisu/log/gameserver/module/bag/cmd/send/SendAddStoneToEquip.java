package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;

public class SendAddStoneToEquip extends BaseMsg {

    public String hashItem;
    public String hashStone;


    public SendAddStoneToEquip() {
        super(CMD.CMD_ADD_STONE_TO_EQUIP);
    }

    public SendAddStoneToEquip(short errorCode) {
        super(CMD.CMD_ADD_STONE_TO_EQUIP, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        data.putUtfString(Params.HASH_WEAPON, hashItem);
        data.putUtfString(Params.HASH_STONE, hashStone);
    }
}
