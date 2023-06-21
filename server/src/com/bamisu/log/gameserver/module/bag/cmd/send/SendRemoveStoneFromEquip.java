package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;

public class SendRemoveStoneFromEquip extends BaseMsg {

    public String hashItem;


    public SendRemoveStoneFromEquip() {
        super(CMD.CMD_REMOVE_STONE_FROM_EQUIP);
    }

    public SendRemoveStoneFromEquip(short errorCode) {
        super(CMD.CMD_REMOVE_STONE_FROM_EQUIP, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;

        data.putUtfString(Params.HASH_WEAPON, hashItem);
    }
}
