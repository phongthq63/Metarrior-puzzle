package com.bamisu.log.gameserver.module.nft.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;

/**
 * Created by Quach Thanh Phong
 * On 3/23/2022 - 9:08 PM
 */
public class SendReturnNFTHeroUpStar extends BaseMsg {

    public SendReturnNFTHeroUpStar() {
        super(CMD.CMD_RETURN_NFT_HERO_UP_STAR);
    }

    public SendReturnNFTHeroUpStar(short errorCode) {
        super(CMD.CMD_RETURN_NFT_HERO_UP_STAR, errorCode);
    }
}
