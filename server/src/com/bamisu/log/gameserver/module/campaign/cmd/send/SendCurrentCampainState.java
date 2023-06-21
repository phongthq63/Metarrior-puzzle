package com.bamisu.log.gameserver.module.campaign.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Map;

/**
 * Create by Popeye on 7:13 PM, 5/19/2020
 */
public class SendCurrentCampainState extends BaseMsg {

    public int area;
    public int state;
    public Map<Byte,Byte> saveStation;

    public SendCurrentCampainState() {
        super(CMD.GET_CURRENT_CAMPAIGN_STATE);
    }

    public SendCurrentCampainState(short errorCode) {
        super(CMD.GET_CURRENT_CAMPAIGN_STATE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        data.putInt(Params.AREA, area);
        data.putInt(Params.STATION, state);

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        for(byte station : saveStation.keySet()){
            objPack = new SFSObject();
            objPack.putInt(Params.STATION, station);
            objPack.putShort(Params.STAR, saveStation.get(station));

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.LIST, arrayPack);
    }
}
