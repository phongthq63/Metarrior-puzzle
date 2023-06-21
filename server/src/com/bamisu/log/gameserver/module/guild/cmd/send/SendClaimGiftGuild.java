package com.bamisu.log.gameserver.module.guild.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Arrays;
import java.util.List;

public class SendClaimGiftGuild extends BaseMsg {

    public String hashGift;
    public List<ResourcePackage> reward;

    public SendClaimGiftGuild() {
        super(CMD.CMD_CLAIM_GIFT_GUILD);
    }

    public SendClaimGiftGuild(short errorCode) {
        super(CMD.CMD_CLAIM_GIFT_GUILD, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putUtfString(Params.HASH, hashGift);

        ISFSArray rewardPack = new SFSArray();
        ISFSObject rewardObj;
        List<String> constantItem = Arrays.asList("RES1025", "RES1026", "MON1014");
        for(ResourcePackage res : reward){
            rewardObj = new SFSObject();

            rewardObj.putUtfString(Params.ID, res.id);
            rewardObj.putLong(Params.AMOUNT, res.amount);
            rewardObj.putBool(Params.DISPLAY, !constantItem.contains(res.id));

            rewardPack.addSFSObject(rewardObj);
        }
        data.putSFSArray(Params.REWARD, rewardPack);
    }
}
