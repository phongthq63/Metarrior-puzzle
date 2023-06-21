package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.datamodel.hero.UserAllHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.UserBlessingHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.datamodel.hunt.entities.MonsterInfo;
import com.bamisu.log.gameserver.datamodel.mage.SageSkillModel;
import com.bamisu.log.gameserver.module.characters.entities.Hero;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.bamisu.log.gameserver.module.hero.entities.HeroInfo;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Quach Thanh Phong
 * On 3/8/2022 - 9:46 PM
 */
public class SendGetIdleHeroData extends BaseMsg {

    public ETeamType teamType;
    public UserAllHeroModel userAllHeroModel;

    public Zone zone;
    public SageSkillModel sageSkillModel;
    public List<String> team;
    public List<HeroModel> listHeroModel;       //List dc deep clone
    public UserBlessingHeroModel userBlessingHeroModel;

    public List<Hero> enemyTeam;
    public List<MonsterInfo> monsterTeam;


    public SendGetIdleHeroData() {
        super(CMD.CMD_GET_IDLE_HERO_DATA);
    }

    public SendGetIdleHeroData(short errorCode) {
        super(CMD.CMD_GET_IDLE_HERO_DATA, errorCode);
    }

    @Override
    public void packData() {
        super.packData();

        ISFSArray arrayPack;
        ISFSObject objPack;

        //Danh sach hero trong team
        data.putUtfStringArray(Params.TEAM, team);
        //Kiem tra team co hero cua friend khong
        List<HeroInfo> teamHeroFriend = HeroManager.getInstance().getListHeroFriendAssistantInTeam(userAllHeroModel.uid, teamType, zone);
        data.putBool(Params.IS_FRIEND, !teamHeroFriend.isEmpty());
        if (!teamHeroFriend.isEmpty()) {
            //List hero lay tu model
            List<HeroModel> listHeroFriend = teamHeroFriend.parallelStream().
                    map(obj -> HeroModel.createByHeroModel(obj.heroModel)).
                    collect(Collectors.toList());

            arrayPack = new SFSArray();
            for (HeroModel heroModel : listHeroFriend) {
                objPack = new SFSObject();

                objPack.putUtfString(Params.HASH_HERO, heroModel.hash);
                objPack.putUtfString(Params.ID, heroModel.id);
                objPack.putShort(Params.LEVEL, heroModel.readLevel());
                objPack.putShort(Params.STAR, heroModel.star);
                objPack.putInt(Params.POWER, HeroManager.getInstance().getPower(heroModel, zone));

                arrayPack.addSFSObject(objPack);
            }

            data.putSFSArray(Params.FRIEND_LIST, arrayPack);
        }

        //Danh sach Hero cua minh
        Set<String> listBlessing = HeroManager.BlessingManager.getInstance().getListHeroSlotBlessing(userBlessingHeroModel, zone).stream().
                map(obj -> obj.hashHero).
                collect(Collectors.toSet());
        arrayPack = new SFSArray();
        boolean blessing;
        for (HeroModel heroModel : listHeroModel) {
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
            objPack.putInt(Params.POWER, HeroManager.getInstance().getPower(heroModel, zone));

            arrayPack.addSFSObject(objPack);
        }
        data.putSFSArray(Params.LIST, arrayPack);
    }
}
