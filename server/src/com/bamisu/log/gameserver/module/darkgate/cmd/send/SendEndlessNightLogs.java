package com.bamisu.log.gameserver.module.darkgate.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.darkgate.model.entities.EndlessNightLogVO;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.SFSArray;

import java.util.List;

/**
 * Create by Popeye on 4:40 PM, 11/26/2020
 */
public class SendEndlessNightLogs extends BaseMsg {
    public List<EndlessNightLogVO> logs;

    public SendEndlessNightLogs() {
        super(CMD.GET_ENDLESS_NIGHT_LOGS);
    }

    public SendEndlessNightLogs(short errorCode) {
        super(CMD.GET_ENDLESS_NIGHT_LOGS, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        if(logs != null){
            ISFSArray isfsArray = new SFSArray();
            data.putSFSArray(Params.LOGS, isfsArray);

            for(EndlessNightLogVO vo : logs){
                isfsArray.addSFSObject(vo.toSFSObject());
            }
        }
    }
}
