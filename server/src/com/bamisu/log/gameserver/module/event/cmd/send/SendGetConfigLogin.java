package com.bamisu.log.gameserver.module.event.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.log.gameserver.module.event.event.login14days.Login14DaysManager;
import com.bamisu.log.gameserver.module.event.event.login14days.configs.Login14DaysConfig;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetConfigLogin extends BaseMsg {
    public Login14DaysConfig config;
    public List<Integer> state;
    public SendGetConfigLogin() {
        super(CMD.CMD_GET_EVENT_LOGIN_CONFIG);
    }

    @Override
    public void packData() {
        super.packData();
        data.putLong(Params.EVENT_START_TIME, config.getStartTime());
        data.putLong(Params.EVENT_END_TIME, config.getEndTime());
        data.putText(Params.ID, config.getId());
        SFSArray arrState = new SFSArray();
        for (int st : state) {
            arrState.addInt(st);
        }

        ISFSArray arrGifts = new SFSArray();
        for (List<ResourcePackage> lst : Login14DaysManager.getInstance().getGiftsConfig()) {
            ISFSArray arrGift = new SFSArray();
            for (ResourcePackage resourcePackage : lst) {
                ISFSObject obj = new SFSObject();
                obj.putInt(Params.AMOUNT, resourcePackage.amount);
                obj.putText(Params.ID, resourcePackage.id);
                arrGift.addSFSObject(obj);
            }

            arrGifts.addSFSArray(arrGift);
        }

        data.putSFSArray(Params.GIFTS, arrGifts);
        data.putSFSArray(Params.LIST, arrState);
    }

}
