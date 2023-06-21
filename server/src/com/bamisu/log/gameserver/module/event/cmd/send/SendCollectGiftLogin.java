package com.bamisu.log.gameserver.module.event.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.List;

public class SendCollectGiftLogin extends BaseMsg {

    public List<ResourcePackage> resourcePackages;
    public String heroId = "";
    public SendCollectGiftLogin() {
        super(CMD.CMD_COLLECT_GIFT_LOGIN);
    }

    public SendCollectGiftLogin(short errorCode) {
        super(CMD.CMD_COLLECT_GIFT_LOGIN, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }

        ISFSArray lst = new SFSArray();
        for (ResourcePackage resourcePackage : resourcePackages) {
            ISFSObject obj = resourcePackage.toSFSObject();
            if (resourcePackage.readId().equalsIgnoreCase("MON1010")) {
                obj.putText(Params.HERO_ID, this.heroId);
            }

            lst.addSFSObject(obj);
        }

        this.data.putSFSArray(Params.LIST, lst);
    }
}
