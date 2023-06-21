package com.bamisu.log.gameserver.module.hero.cmd.send;

import com.bamisu.log.gameserver.datamodel.hero.UserAllHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.UserBlessingHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.log.gameserver.datamodel.hunt.entities.MonsterInfo;
import com.bamisu.log.gameserver.datamodel.mage.SageSkillModel;
import com.bamisu.log.gameserver.module.characters.entities.Hero;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.bamisu.log.gameserver.module.hero.entities.HeroInfo;
import com.bamisu.gamelib.skill.SkillConfigManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SendLoadSceneTeamHero extends BaseMsg {

    public ETeamType teamType;
    public UserAllHeroModel userAllHeroModel;

    public Zone zone;
    public SageSkillModel sageSkillModel;
    public List<String> team;
    public List<HeroModel> listHeroModel;       //List dc deep clone
    public UserBlessingHeroModel userBlessingHeroModel;

    //enemy info if player battle
//    public List<HeroModel> listHeroEnemyModel;
//    public UserBlessingHeroModel enemyUserBlessingHeroModel;
    public List<Hero> enemyTeam;
    public List<MonsterInfo> monsterTeam;


    public SendLoadSceneTeamHero() {
        super(CMD.CMD_LOAD_SCENE_TEAM_HERO);
    }

    public SendLoadSceneTeamHero(short errorCode) {
        super(CMD.CMD_LOAD_SCENE_TEAM_HERO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) return;

        ISFSArray arrayPack;
        ISFSObject objPack;

        //Danh sach hero trong team
        data.putUtfStringArray(Params.TEAM, team);
        //Kiem tra team co hero cua friend khong
        List<HeroInfo> teamHeroFriend = new ArrayList<>();
//        List<HeroInfo> teamHeroFriend = HeroManager.getInstance().getListHeroFriendAssistantInTeam(userAllHeroModel.uid, teamType, zone);
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
//        Set<String> listBlessing = HeroManager.BlessingManager.getInstance().getListHeroSlotBlessing(userBlessingHeroModel, zone).stream().
//                map(obj -> obj.hashHero).
//                collect(Collectors.toSet());
        Set<String> listBlessing = new HashSet<>();
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

        //team enemy nếu đang battle với người khác
        switch (teamType){
            case PVP_OFFLINE:
            case ARENA:
                arrayPack = new SFSArray();
                for (Hero hero : enemyTeam) {
                    if (hero != null) {
                        objPack = new SFSObject();
                        objPack.putUtfString(Params.ID, hero.readID());
                        objPack.putShort(Params.LEVEL, (short) hero.readLevel());
                        objPack.putShort(Params.STAR, (short) hero.readStar());
                        objPack.putInt(Params.POWER, HeroManager.getInstance().getPower(hero.readStats()));
                        arrayPack.addSFSObject(objPack);
                    }
                }
                data.putSFSArray(Params.ENEMY_TEAM, arrayPack);
                break;
            case MONSTER_HUNT:
                arrayPack = new SFSArray();
                for(MonsterInfo monster : monsterTeam){
                    if (monster != null) {
                        objPack = new SFSObject();
                        objPack.putUtfString(Params.ID, monster.monster.id);
                        objPack.putShort(Params.LEVEL, (short) monster.monster.level);
                        objPack.putShort(Params.STAR, (short) monster.monster.star);
                        objPack.putUtfString(Params.ELEMENT, monster.monster.element);
                        objPack.putFloat(Params.CURRENT_HP, (short) monster.currentHp);
                        arrayPack.addSFSObject(objPack);
                    }else {
                        arrayPack.addNull();
                    }
                }
                data.putSFSArray(Params.ENEMY_TEAM, arrayPack);
                break;
        }

        //skill có thể đem vào của pháp sư
        SFSArray arraySkillSage = new SFSArray();
        for (SkillInfo skillInfo : sageSkillModel.skills) {
            if (SkillConfigManager.getInstance().getSageSkill(skillInfo.id).type.equalsIgnoreCase("Active")) {
                arraySkillSage.addSFSObject(SFSObject.newFromJsonData(Utils.toJson(skillInfo)));
            }
        }
        data.putSFSArray(Params.SAGE_SKILL_LIST, arraySkillSage);

        //skill đã được chọn trước đó
        List<String> currentSkill = new ArrayList<>();
        for (SkillInfo skillInfo : sageSkillModel.currentSkill) {
            currentSkill.add(skillInfo.id);
        }
        data.putUtfStringArray(Params.SAGE_CURRENT_SKILL_LIST, currentSkill);
    }
}
