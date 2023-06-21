package com.bamisu.log.gameserver.module.arena.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.manager.UserManager;
import com.bamisu.log.gameserver.datamodel.arena.UserArenaModel;
import com.bamisu.log.gameserver.datamodel.arena.entities.UserArenaInfo;
import com.bamisu.log.gameserver.datamodel.hero.UserAllHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.UserBlessingHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.arena.ArenaManager;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SendLoadSceneArena extends BaseMsg {

    public UserModel userModel;
    public Long userRank;
    public List<UserArenaInfo> rank;
    public UserManager userManager;
    public UserArenaModel userArenaModel;
    public int timeEndSeason;

    public UserAllHeroModel userAllHeroModel;
    public List<String> team;
    public List<HeroModel> listHeroModel;       //List dc deep clone
    public UserBlessingHeroModel userBlessingHeroModel;

    public Zone zone;


    public SendLoadSceneArena() {
        super(CMD.CMD_LOAD_SCENE_ARENA);
    }

    @Override
    public void packData() {
        super.packData();

        data.putInt(Params.TIME, timeEndSeason);

        //Rank
        ISFSArray arrayPack = new SFSArray();
        ISFSObject objPack;
        UserModel userModel;
        for(int index = 0; index < rank.stream().limit(ArenaManager.getInstance().getCountRankShow()).collect(Collectors.toList()).size(); index++){
            objPack = new SFSObject();
            userModel = userManager.getUserModel(rank.get(index).uid);

            objPack.putLong(Params.UID, userModel.userID);
            objPack.putUtfString(Params.AVATAR_ID, userModel.avatar);
            objPack.putInt(Params.FRAME, userModel.avatarFrame);
            objPack.putUtfString(Params.NAME, userModel.displayName);
            objPack.putInt(Params.LEVEL, BagManager.getInstance().getLevelUser(userModel.userID, userManager.getZone()));
            objPack.putInt(Params.POWER, HeroManager.getInstance().getTeamPower(userModel.userID, ETeamType.ARENA_DEFENSE, userManager.getZone(), true));
            objPack.putInt(Params.RANK, index + 1);
            objPack.putInt(Params.POINT, rank.get(index).point);

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.RANK, arrayPack);

        //User
        objPack = new SFSObject();
        objPack.putLong(Params.UID, this.userModel.userID);
        objPack.putUtfString(Params.AVATAR_ID, this.userModel.avatar);
        objPack.putInt(Params.FRAME, this.userModel.avatarFrame);
        objPack.putUtfString(Params.NAME, this.userModel.displayName);
        objPack.putInt(Params.LEVEL, BagManager.getInstance().getLevelUser(this.userModel.userID, userManager.getZone()));
        objPack.putInt(Params.POWER, HeroManager.getInstance().getTeamPower(this.userModel.userID, ETeamType.ARENA_DEFENSE, userManager.getZone(), true));
        objPack.putInt(Params.RANK, (userRank == null) ? -1 : userRank.intValue() + 1);
        objPack.putInt(Params.POINT, userArenaModel.readArenaPoint());
        data.putSFSObject(Params.USER_INFO, objPack);

        //Team
        //Danh sach Hero team cua minh
        Set<String> listBlessing = HeroManager.BlessingManager.getInstance().getListHeroSlotBlessing(userBlessingHeroModel, userManager.getZone()).stream().
                map(obj -> obj.hashHero).
                collect(Collectors.toSet());
        arrayPack = new SFSArray();
        boolean blessing;
        for (HeroModel heroModel : listHeroModel) {
            if(!team.contains(heroModel.hash)) continue;
            objPack = new SFSObject();

            objPack.putUtfString(Params.HASH_HERO, heroModel.hash);
            objPack.putUtfString(Params.ID, heroModel.id);

            blessing = listBlessing.contains(heroModel.hash);
            objPack.putBool(Params.BLESSING, blessing);
            //Neu dc ban phuoc
            if (blessing) {
                heroModel.level = (short) HeroManager.BlessingManager.getInstance().getLevelBlessingHero(userAllHeroModel, heroModel.id);
            }
            //Neu khong dc ban phuoc
            objPack.putShort(Params.LEVEL, heroModel.readLevel());
            objPack.putShort(Params.STAR, heroModel.star);

            objPack.putInt(Params.POWER, HeroManager.getInstance().getPower(HeroManager.getInstance().getStatsHero(heroModel, zone)));

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.TEAM, arrayPack);

        //So lan free
        data.putShort(Params.FREE, (short) userArenaModel.readCountFightFree(userManager.getZone()));
    }
}
