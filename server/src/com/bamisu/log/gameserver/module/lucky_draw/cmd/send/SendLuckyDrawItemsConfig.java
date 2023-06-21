package com.bamisu.log.gameserver.module.lucky_draw.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.TokenResourcePackage;
import com.bamisu.log.gameserver.module.lucky_draw.config.entities.LuckyDrawVO;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendLuckyDrawItemsConfig extends BaseMsg {
    public SendLuckyDrawItemsConfig() {
        super(CMD.CMD_GET_LUCKY_DRAW_ITEMS);
    }
    public SendLuckyDrawItemsConfig(short errorCode) {
        super(CMD.CMD_GET_LUCKY_DRAW_ITEMS,errorCode);
    }
    public List<LuckyDrawVO> listItem;
    public long amountTicket;

    @Override
    public void packData() {
        super.packData();
        if(isError())return;
        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        ISFSObject objPackInfo = new SFSObject();
        //ResourcePackage resourcePackage = new ResourcePackage();
        TokenResourcePackage tokenResourcePackage = new TokenResourcePackage();
        for(LuckyDrawVO obj : listItem){
            objPack = new SFSObject();
            objPack.putUtfString(Params.ID, obj.item_id);
            objPack.putSFSObject("reward", obj.reward.get(0).toSFSObjects());
            arrayPack.addSFSObject(objPack);
        }
        objPackInfo.putLong("tickets",amountTicket);
        objPackInfo.putSFSArray("list_item",arrayPack);
        data.putSFSObject("info",objPackInfo );
    }
}
