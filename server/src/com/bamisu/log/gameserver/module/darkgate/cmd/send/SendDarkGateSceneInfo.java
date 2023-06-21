package com.bamisu.log.gameserver.module.darkgate.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.darkgate.entities.EventStatus;
import com.mysql.fabric.xmlrpc.base.Param;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Create by Popeye on 2:35 PM, 11/12/2020
 */
public class SendDarkGateSceneInfo extends BaseMsg {
    public SendDarkGateSceneInfo(int cmd) {
        super(cmd);
    }

    public SendDarkGateSceneInfo(int cmd, short errorCode) {
        super(cmd, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;
    }

    public void pushEvent(int id, EventStatus eventStatus, int time, String bossID, String bossElement, String bossKingdom, int bossLevel) {
        if(!data.containsKey(Params.EVENTS)){
            data.putSFSArray(Params.EVENTS, new SFSArray());
        }
        ISFSArray sfsArray = data.getSFSArray(Params.EVENTS);

        SFSObject sfsObject = new SFSObject();
        sfsObject.putInt(Params.ID, id);
        sfsObject.putInt(Params.STATUS, eventStatus.intValue);
        sfsObject.putInt(Params.TIME, time);
        sfsObject.putUtfString(Params.BOSS_ID, bossID);
        sfsObject.putUtfString(Params.ELEMENT, bossElement);
        sfsObject.putUtfString(Params.KINGDOM, bossKingdom);
        sfsObject.putInt(Params.BOSS_LEVEL, bossLevel);
        sfsObject.putInt(Params.ENEMY_POWER, 1000000);

        sfsArray.addSFSObject(sfsObject);
    }
}
