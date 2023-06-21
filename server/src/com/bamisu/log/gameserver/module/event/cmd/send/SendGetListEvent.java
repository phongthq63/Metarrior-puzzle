package com.bamisu.log.gameserver.module.event.cmd.send;

import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.event.EventInGameManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.event.config.entities.EventInGameVO;
import com.bamisu.log.gameserver.module.event.event.login14days.Login14DaysManager;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Map;

public class SendGetListEvent extends BaseMsg {

    public Map<String,Integer> listEvent;

    public SendGetListEvent() {
        super(CMD.CMD_GET_LIST_EVENT);
    }

    @Override
    public void packData() {
        super.packData();

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        EventInGameVO cf;
        int now = Utils.getTimestampInSecond();
        boolean isActiveLoginEvent = Login14DaysManager.getInstance().isActiveEvent();
        for(String key : listEvent.keySet()){
            if (key.equalsIgnoreCase("event3") && !isActiveLoginEvent) {
                continue;
            }

            objPack = new SFSObject();
            cf = EventInGameManager.getInstance().getEventConfig(key);

            objPack.putUtfString(Params.ID, key);
            if(cf != null){
                objPack.putInt(Params.TIME,
                        TimeUtils.getDeltaTimeToTime(ETimeType.fromID(cf.timeShow), listEvent.get(key)));
            }else {
                objPack.putInt(Params.TIME, (listEvent.get(key) != -1) ? listEvent.get(key) - now : -1);
            }


            arrayPack.addSFSObject(objPack);
        }

        if (isActiveLoginEvent) {
            objPack = new SFSObject();
            objPack.putText(Params.ID, "event3");
            objPack.putInt(Params.TIME, 1);
            arrayPack.addSFSObject(objPack);
        }

        data.putSFSArray(Params.LIST, arrayPack);
    }
}
