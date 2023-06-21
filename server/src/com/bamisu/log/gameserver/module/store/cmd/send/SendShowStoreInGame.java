package com.bamisu.log.gameserver.module.store.cmd.send;

import com.bamisu.log.gameserver.module.store.StoreManager;
import com.bamisu.log.gameserver.module.store.entities.StoreDataVO;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;


public class SendShowStoreInGame extends BaseMsg {
    public StoreDataVO list;
    public SendShowStoreInGame() {
        super(CMD.CMD_SHOW_STORE_IN_GAME);
    }

    public SendShowStoreInGame(short errorCode) {
        super(CMD.CMD_SHOW_STORE_IN_GAME, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError())return;
        int delta = (int)StoreManager.getInstance().getDeltaTime(list.time);
        SFSObject sfsObject = new SFSObject();
        sfsObject.putInt(Params.ID_STONE, list.idStore);
        sfsObject.putInt(Params.TIME, delta);
        sfsObject.putInt(Params.COUNT, list.count);
        SFSArray sfsArray = new SFSArray();
        for (String vo: list.listItem){
            SFSObject value = new SFSObject();
            String[] data = vo.split("-");
            value.putInt(Params.SLOT, Integer.parseInt(data[0]));
            value.putInt(Params.POSITION, Integer.parseInt(data[1]));
            value.putBool(Params.STATUS, Boolean.parseBoolean(data[2]));
            value.putInt(Params.DISCOUNT, Integer.parseInt(data[3]));
            sfsArray.addSFSObject(value);
        }
        sfsObject.putSFSArray(Params.LIST, sfsArray);
        data.putSFSObject(Params.RESOURCE, sfsObject);
    }
}
