package com.bamisu.log.gameserver.module.tower.cmd.send;

import com.bamisu.log.gameserver.datamodel.tower.UserTowerModel;
import com.bamisu.log.gameserver.datamodel.tower.entities.UserTowerInfo;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.manager.UserManager;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetRankTower extends BaseMsg {

    public UserModel userModel;
    public List<UserTowerInfo> listRanker;
    public UserTowerModel userTowerModel;
    public UserManager userManager;
    public Zone zone;

    public SendGetRankTower() {
        super(CMD.CMD_GET_RANK_TOWER);
    }

    public SendGetRankTower(short errorCode) {
        super(CMD.CMD_GET_RANK_TOWER, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        UserModel userModel;
        for(UserTowerInfo info : listRanker){
            objPack = new SFSObject();
            userModel = userManager.getUserModel(info.uid);

            objPack.putLong(Params.UID, userModel.userID);
            objPack.putUtfString(Params.AVATAR_ID, userModel.avatar);
            objPack.putInt(Params.FRAME, userModel.avatarFrame);
            objPack.putUtfString(Params.NAME, userModel.displayName);
            objPack.putInt(Params.LEVEL, BagManager.getInstance().getLevelUser(userModel.userID, userManager.getZone()));
            objPack.putInt(Params.POWER, HeroManager.getInstance().getPower(userModel.userID, userManager.getZone()));
            objPack.putShort(Params.FLOOR, info.floor);
            objPack.putInt(Params.TIME, info.timeStamp);

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.LIST, arrayPack);

        objPack = new SFSObject();
        objPack.putLong(Params.UID, this.userModel.userID);
        objPack.putUtfString(Params.AVATAR_ID, this.userModel.avatar);
        objPack.putInt(Params.FRAME, this.userModel.avatarFrame);
        objPack.putUtfString(Params.NAME, this.userModel.displayName);
        objPack.putInt(Params.LEVEL, BagManager.getInstance().getLevelUser(this.userModel.userID, userManager.getZone()));
        objPack.putInt(Params.POWER, HeroManager.getInstance().getPower(this.userModel.userID, userManager.getZone()));
        objPack.putShort(Params.FLOOR, userTowerModel.floor);
        objPack.putInt(Params.TIME, userTowerModel.timeStamp);
        data.putSFSObject(Params.USER_INFO, objPack);
    }
}
