package com.bamisu.log.gameserver.module.lucky_draw.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class SendLuckyDrawItem extends BaseMsg {
    public SendLuckyDrawItem() {
        super(CMD.CMD_GET_RESULT_LUCKY_DRAW);
    }
    public SendLuckyDrawItem(short errorCode) {super(CMD.CMD_GET_RESULT_LUCKY_DRAW,errorCode);}

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    private String item_id;
    @Override
    public void packData() {
        super.packData();
        if(isError())return;
        ISFSObject objPack = new SFSObject();
        objPack.putUtfString(Params.ID, item_id);
        data.putSFSObject("result",objPack);
    }
}
