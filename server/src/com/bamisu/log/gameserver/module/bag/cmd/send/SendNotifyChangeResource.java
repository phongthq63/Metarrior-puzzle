package com.bamisu.log.gameserver.module.bag.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;

/**
 * Create by Popeye on 3:41 PM, 1/10/2020
 */
public class SendNotifyChangeResource extends BaseMsg {
    public ISFSArray arrayCurrent;

    public SendNotifyChangeResource() {
        super(CMD.NOTIFY_USER_BALANCE_CHANGE);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }
        data.putSFSArray(Params.CURRENT_RESOURCE, arrayCurrent);
    }
}
