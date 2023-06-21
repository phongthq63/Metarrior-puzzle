package com.bamisu.log.gameserver.module.nft.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

/**
 * Created by Quach Thanh Phong
 * On 3/3/2022 - 12:47 AM
 */
public class SendRemoveMintNFTHero extends BaseMsg {

    public SendRemoveMintNFTHero() {
        super(CMD.CMD_REMOVE_MINT_NFT_HERO);
    }

    public SendRemoveMintNFTHero(short errorCode) {
        super(CMD.CMD_REMOVE_MINT_NFT_HERO, errorCode);
    }
}
