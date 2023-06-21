package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.TokenResourcePackage;
import com.smartfoxserver.v2.entities.data.SFSArray;

import java.util.Collection;
import java.util.List;

/**
 * Create by Popeye on 5:26 PM, 4/21/2020
 */
public class SendActiveGiftcode extends BaseMsg {
    public SFSArray arrResources = new SFSArray();

    public SendActiveGiftcode() {
        super(CMD.CMD_ACTIVE_GIFTCODE);
    }

    public SendActiveGiftcode(short errorCode) {
        super(CMD.CMD_ACTIVE_GIFTCODE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putSFSArray(Params.GIFT, arrResources);
    }

    public SendActiveGiftcode pushGift(ResourcePackage resource){
        arrResources.addSFSObject(resource.toSFSObject());
        return this;
    }

    public SendActiveGiftcode pushGift(Collection<? extends ResourcePackage> resourcePackageList){
        resourcePackageList.forEach(obj -> {
            if (obj instanceof TokenResourcePackage) {
                arrResources.addSFSObject(((TokenResourcePackage) obj).toSFSObject2());
            } else {
                arrResources.addSFSObject(obj.toSFSObject());
            }
        });

        return this;
    }
}
