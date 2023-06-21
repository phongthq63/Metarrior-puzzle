package com.bamisu.log.gameserver.module.event.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.event.UserEventDataModel;
import com.bamisu.log.gameserver.datamodel.event.entities.EventDataInfo;
import com.bamisu.log.gameserver.module.event.defind.EEventInGame;
import com.bamisu.log.gameserver.module.event.event.christmas.ChristmasEventManager;
import com.bamisu.log.gameserver.module.event.event.christmas.config.entities.ExchangeChristmasVO;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.Map;

public class SendGetInfoEvent extends BaseMsg {

    public String idEvent;
    public UserEventDataModel userEventDataModel;
    public Map<String,Integer> listEventGeneral;
    public Map<String,Integer> listEventSpecial;
    public Zone zone;

    //Christmas
    public List<ExchangeChristmasVO> christmasShopCf;


    public SendGetInfoEvent() {
        super(CMD.CMD_GET_INFO_EVENT);
    }

    public SendGetInfoEvent(short errorCode) {
        super(CMD.CMD_GET_INFO_EVENT, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putUtfString(Params.ID, idEvent);

        int now = Utils.getTimestampInSecond();
        ISFSArray arrayPack;
        ISFSObject objPack;
        switch (EEventInGame.fromID(idEvent)){
            case CHRISSMATE:
                if(ChristmasEventManager.getInstance().isTimeEndEvent(zone)) break;

                arrayPack = new SFSArray();
                EventDataInfo eventData = userEventDataModel.readEventDataInfo(idEvent);

                for(ExchangeChristmasVO cf : christmasShopCf){
                    objPack = new SFSObject();

                    objPack.putUtfString(Params.ID, cf.id);
                    objPack.putSFSArray(Params.COST, SFSArray.newFromJsonData(Utils.toJson(cf.cost)));
                    objPack.putSFSArray(Params.REWARD, SFSArray.newFromJsonData(Utils.toJson(cf.reward)));
                    objPack.putInt(Params.MAX, cf.buy);
                    objPack.putInt(Params.BUY, eventData.readCountBuyChristmas(cf.id));

                    arrayPack.addSFSObject(objPack);
                }

                data.putSFSArray(Params.LIST, arrayPack);

                data.putInt(Params.TIME, (listEventGeneral.getOrDefault(idEvent, -1) != -1) ? listEventGeneral.get(idEvent) - now : -1);
                break;
        }
    }
}
