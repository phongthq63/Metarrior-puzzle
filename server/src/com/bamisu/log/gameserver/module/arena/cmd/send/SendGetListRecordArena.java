package com.bamisu.log.gameserver.module.arena.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.manager.UserManager;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.arena.entities.RecordArenaInfo;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetListRecordArena extends BaseMsg {

    public List<RecordArenaInfo> list;
    public UserManager userManager;

    public SendGetListRecordArena() {
        super(CMD.CMD_GET_LIST_RECORD_ARENA);
    }

    @Override
    public void packData() {
        super.packData();

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        ISFSObject userPack;
        UserModel userModel;
        int now = Utils.getTimestampInSecond();
        for(RecordArenaInfo data : list){
            objPack = new SFSObject();
            userPack = new SFSObject();
            userModel = userManager.getUserModel(data.enemy);

            objPack.putUtfString(Params.ID, data.hashRecord);
            objPack.putBool(Params.WIN, data.win == data.uid);
            objPack.putInt(Params.POINT, data.point);
            objPack.putInt(Params.TIME, now - data.timeStamp);

            userPack.putLong(Params.UID, userModel.userID);
            userPack.putUtfString(Params.NAME, userModel.displayName);
            userPack.putUtfString(Params.AVATAR_ID, userModel.avatar);
            userPack.putInt(Params.FRAME, userModel.avatarFrame);
            userPack.putInt(Params.LEVEL, BagManager.getInstance().getLevelUser(userModel.userID, userManager.getZone()));
            userPack.putInt(Params.POWER, HeroManager.getInstance().getPower(userModel.userID, userManager.getZone()));

            objPack.putSFSObject(Params.USER_INFO, userPack);

            arrayPack.addSFSObject(objPack);
        }

        data.putSFSArray(Params.LIST, arrayPack);
    }
}
