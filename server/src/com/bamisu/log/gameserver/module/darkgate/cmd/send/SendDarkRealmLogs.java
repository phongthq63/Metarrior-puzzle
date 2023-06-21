package com.bamisu.log.gameserver.module.darkgate.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.darkgate.model.entities.DarkRealmLogVO;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.SFSArray;

import java.util.List;

/**
 * Create by Popeye on 4:40 PM, 11/26/2020
 */
public class SendDarkRealmLogs extends BaseMsg {
    public List<DarkRealmLogVO> logs;

    public SendDarkRealmLogs() {
        super(CMD.GET_DARK_REALM_LOGS);
    }

    public SendDarkRealmLogs(short errorCode) {
        super(CMD.GET_DARK_REALM_LOGS, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        if(logs != null){
            ISFSArray isfsArray = new SFSArray();
            data.putSFSArray(Params.LOGS, isfsArray);

            for(DarkRealmLogVO vo : logs){
                isfsArray.addSFSObject(vo.toSFSObject());
            }
        }
    }
}
