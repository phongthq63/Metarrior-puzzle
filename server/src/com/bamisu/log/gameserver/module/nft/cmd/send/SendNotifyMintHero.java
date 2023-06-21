package com.bamisu.log.gameserver.module.nft.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.log.gameserver.module.nft.NFTManager;

public class SendNotifyMintHero extends BaseMsg {

    public SendNotifyMintHero(long uid) {
        super(CMD.CMD_NOTIFY_MINT_HERO);
        NFTManager.getInstance().notifySyncHero(uid);
    }
}
