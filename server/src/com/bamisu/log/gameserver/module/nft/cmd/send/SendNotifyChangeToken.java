package com.bamisu.log.gameserver.module.nft.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;

/**
 * Created by Quach Thanh Phong
 * On 3/8/2022 - 11:20 PM
 */
public class SendNotifyChangeToken extends BaseMsg {
    public ISFSArray arrayCurrent;

    public SendNotifyChangeToken() {
        super(CMD.NOTIFY_USER_BALANCE_CHANGE);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }
        data.putSFSArray(Params.CURRENT_TOKEN, arrayCurrent);
    }
}
