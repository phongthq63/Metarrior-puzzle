package com.bamisu.log.gameserver.module.arena.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.manager.UserManager;
import com.bamisu.log.gameserver.module.arena.ArenaManager;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendRefreshListFightArena extends BaseMsg {

    public UserManager userManager;
    public List<Long> enemy;

    public SendRefreshListFightArena() {
        super(CMD.CMD_REFRESH_LIST_FIGHT_ARENA);
    }

    public SendRefreshListFightArena(short errorCode) {
        super(CMD.CMD_REFRESH_LIST_FIGHT_ARENA, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        UserModel userModel;
        for(long uid : enemy){
            objPack = new SFSObject();
            userModel = userManager.getUserModel(uid);

            objPack.putLong(Params.UID, userModel.userID);
            objPack.putUtfString(Params.AVATAR_ID, userModel.avatar);
            objPack.putInt(Params.FRAME, userModel.avatarFrame);
            objPack.putUtfString(Params.NAME, userModel.displayName);
            objPack.putInt(Params.LEVEL, BagManager.getInstance().getLevelUser(userModel.userID, userManager.getZone()));
            objPack.putInt(Params.POWER, HeroManager.getInstance().getTeamPower(userModel.userID, ETeamType.ARENA_DEFENSE, userManager.getZone(), true));
            objPack.putInt(Params.POINT, ArenaManager.getInstance().getUserArenaPoint(uid, userManager.getZone()));

            arrayPack.addSFSObject(objPack);
        }

        data.putSFSArray(Params.LIST, arrayPack);
    }
}
