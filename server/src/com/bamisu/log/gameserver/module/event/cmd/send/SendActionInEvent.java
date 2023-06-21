package com.bamisu.log.gameserver.module.event.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSArray;

import java.util.List;

public class SendActionInEvent extends BaseMsg {

    public String id;
    public int count;
    public List<ResourcePackage> reward;



    public SendActionInEvent() {
        super(CMD.CMD_ACTION_IN_EVENT);
    }

    public SendActionInEvent(short errorCode) {
        super(CMD.CMD_ACTION_IN_EVENT, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.ID, id);
        data.putInt(Params.COUNT, count);
        data.putSFSArray(Params.REWARD, SFSArray.newFromJsonData(Utils.toJson(reward)));
    }
}
