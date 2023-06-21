package com.bamisu.log.gameserver.module.hunt;

import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.entities.IDPrefix;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.item.entities.MoneyPackageVO;
import com.bamisu.gamelib.sql.hunt.HuntRewardDBO;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.hunt.UserHuntModel;
import com.bamisu.log.gameserver.datamodel.hunt.entities.HuntInfo;
import com.bamisu.log.gameserver.datamodel.hunt.entities.MonsterInfo;
import com.bamisu.log.gameserver.datamodel.mage.SageSkillModel;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.campaign.entities.HeroPackage;
import com.bamisu.log.gameserver.module.characters.entities.*;
import com.bamisu.log.gameserver.module.friends.FriendHeroManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.bamisu.log.gameserver.module.hero.entities.HeroPosition;
import com.bamisu.log.gameserver.module.hero.exception.InvalidUpdateTeamException;
import com.bamisu.log.gameserver.module.hunt.cmd.send.SendDoHunt;
import com.bamisu.log.gameserver.module.hunt.cmd.send.SendRewardHunt;
import com.bamisu.log.gameserver.module.hunt.config.HuntConfig;
import com.bamisu.log.gameserver.module.hunt.config.HuntRewardConfig;
import com.bamisu.log.gameserver.module.hunt.config.entities.RewardPowerVO;
import com.bamisu.log.gameserver.module.ingame.FightingCreater;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingType;
import com.bamisu.log.gameserver.sql.hunt.dao.HuntDao;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HuntManager {

    private HuntConfig huntConfig;
    private HuntRewardConfig rewardHuntConfig;


    private static HuntManager ourInstance = new HuntManager();

    public static HuntManager getInstance() {
        return ourInstance;
    }

    private HuntManager() {
        loadConfig();
    }

    private void loadConfig() {
        huntConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Hunt.FILE_PATH_CONFIG_HUNT), HuntConfig.class);
        rewardHuntConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Hunt.FILE_PATH_CONFIG_REWARD_HUNT), HuntRewardConfig.class);
    }




    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * Lay user hunt model
     *
     * @param uid
     * @param zone
     * @return
     */
    public UserHuntModel getUserHuntModel(long uid, Zone zone) {
        UserHuntModel userHuntModel = UserHuntModel.copyFromDBtoObject(uid, zone);
        if (userHuntModel == null) {
            userHuntModel = UserHuntModel.createUserHuntModel(uid, zone);
        }

        if (userHuntModel.huntInfo.listEnemy == null) {
            userHuntModel.huntInfo = userHuntModel.genHunt(uid, zone);
            userHuntModel.saveToDB(zone);
        }

        return userHuntModel;
    }

    /**
     * Lay thong tin hunt
     *
     * @param uid
     * @param zone
     * @return
     */
    public HuntInfo getUserHuntInfo(long uid, Zone zone) {
        UserHuntModel userHuntModel = getUserHuntModel(uid, zone);
        return getUserHuntInfo(userHuntModel, zone);
    }

    public HuntInfo getUserHuntInfo(UserHuntModel userHuntModel, Zone zone) {
        return userHuntModel.readHunt(zone);
    }

    public boolean refreshHuntUserHuntModel(UserHuntModel userHuntModel, Zone zone) {
        userHuntModel.refreshHunt(zone);
        return true;
    }

    /**
     * Thuc hien hunt
     */
    public void complateHunt(Zone zone, Map<String, Object> statisticals, Collection<Float> remainingHPList, boolean isWin, long uid) {
        HuntHandler huntHandler = ((HuntHandler) ((ZoneExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_HUNT));
        User user = ExtensionUtility.getInstance().getUserById(uid);
        UserHuntModel userHuntModel = getUserHuntModel(uid, zone);
        List<ResourcePackage> rewardComplete = new ArrayList<>();

        if (isWin) {
            userHuntModel.winHunt++;

            HuntInfo huntInfo = getUserHuntInfo(userHuntModel, zone);
            rewardComplete.addAll(getRewardWinHuntConfig());
            rewardComplete.addAll(huntInfo.reward);

            // save to DB
            if (huntInfo.reward.size() == 5) {
                String type = huntInfo.reward.get(4).id;
                int amount = huntInfo.reward.get(4).amount;
                if (createHuntHistory(uid, type, amount, zone)) {
                }
            }

            //Lam moi hunt
            refreshHuntUserHuntModel(userHuntModel, zone);

            if (HeroManager.getInstance().haveHeroFriendAssistantInTeam(uid, ETeamType.MONSTER_HUNT, zone)) {
                //Update so lan co the muon hero
                FriendHeroManager.getInstance().updateCountAssignHeroFriend(uid, ETeamType.MONSTER_HUNT, isWin, zone);
                //Kiem tra xem co the muon khong
                if (!FriendHeroManager.getInstance().canAssignHeroFriend(uid, ETeamType.MONSTER_HUNT, zone)) {
                    //Neu khong the tu clear khoi team
                    HeroManager.getInstance().clearHeroFriendTeamHero(uid, ETeamType.MONSTER_HUNT, zone);
                }
            }
        } else {
            rewardComplete.addAll(getRewardLoseHuntConfig());

            //Thua trong hunt
            userHuntModel.increaseLose(zone);

            //Update hp enemy
            userHuntModel.updateHpEnemy(new ArrayList<>(remainingHPList), zone);
        }

        // create vao da lam xong mission va Them phan thuong vao tui
        if (!BagManager.getInstance().addItemToDB(
                rewardComplete.stream().collect(Collectors.toMap(obj -> obj.id, Function.identity(), (keyOld, keyNew) -> new MoneyPackageVO(keyOld.id, keyOld.amount + keyNew.amount))).values(),
                uid,
                zone,
                UserUtils.TransactionType.COMPLETE_HUNT)) {
            SendRewardHunt objPut = new SendRewardHunt(ServerConstant.ErrorCode.ERR_SYS);
            huntHandler.send(objPut, user);
        }

        SendRewardHunt objPut = new SendRewardHunt();
        huntHandler.send(objPut, user);

        //Event
        Map<String, Object> data = new HashMap<>();
        data.put(Params.WIN, isWin);
        data.put(Params.COUNT_WIN, userHuntModel.winHunt);
        data.put(Params.COUNT_LOSE, userHuntModel.readLose());
        data.putAll(statisticals);
        GameEventAPI.ariseGameEvent(EGameEvent.FINISH_HUNT_FIGHTING, uid, data, zone);
    }

    public boolean createHuntHistory(long userId, String type, int amount, Zone zone) {
        HuntDao huntDao = new HuntDao();
        HuntRewardDBO obj = new HuntRewardDBO();
        obj.userId = userId;
        obj.type = type;
        obj.amount = amount;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LocalDateTime current = LocalDateTime.now(ZoneId.of("UTC"));
        java.util.Date now;
        try {
            now = sdf.parse(current.format(formatter));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        obj.date = now;

        return huntDao.createHuntRewardToDB(zone, obj);
    }

    public void doHunt(User user, long uid, List<HeroPosition> update, HuntHandler huntHandler, Collection<String> sageSkill) {
        Zone zone = huntHandler.getParentExtension().getParentZone();
        UserHuntModel userHuntModel = HuntManager.getInstance().getUserHuntModel(uid, zone);
        HuntInfo huntInfo = userHuntModel.readHunt(zone);

        //update enemy hunt
        try {
            HeroManager.getInstance().doUpdateTeamHero(huntHandler.getParentExtension().getParentZone(), uid, ETeamType.MONSTER_HUNT.getId(), update);
        } catch (InvalidUpdateTeamException e) {
            e.printStackTrace();
            return;
        }

        //Kiem tra tai nguyen danh hunt
        if (!BagManager.getInstance().addItemToDB(getCostFightHunt(), uid, zone, UserUtils.TransactionType.DO_HUNT)) {
            SendDoHunt objPut = new SendDoHunt(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
            huntHandler.send(objPut, user);
            return;
        }

        //update sage skill
        SageSkillModel sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
        sageSkillModel.updateCurrentSkill(zone, sageSkill);

        //create hunt fighting room
        CreateRoomSettings cfg = new CreateRoomSettings();
        cfg.setName(Utils.ranStr(10));
        cfg.setGroupId(ConfigHandle.instance().get(Params.HUNT_ROOM_GROUP_ID));
        cfg.setMaxUsers(1);
        cfg.setDynamic(true);
        cfg.setAutoRemoveMode(SFSRoomRemoveMode.NEVER_REMOVE);
        cfg.setGame(true);

        Map<Object, Object> props = new HashMap<>();
        props.put(Params.UID, uid);
        props.put(Params.WIN_CONDITIONS, huntInfo.condition);
        props.put(Params.HUNT_INFO, huntInfo);

        //Team NPC
        List<ICharacter> npcTeam = new ArrayList<>();
        List<Float> remainingHP = new ArrayList<>();
        for (MonsterInfo monsterInfo : huntInfo.listEnemy) {
            if (monsterInfo == null) { //slot trống
                npcTeam.add(null);
                remainingHP.add(0f);
                continue;
            }

            if (monsterInfo.monster.id.isEmpty()) { //slot trống
                npcTeam.add(null);
                remainingHP.add(0f);
                continue;
            }

            remainingHP.add(monsterInfo.currentHp);

            if (monsterInfo.monster.id.indexOf(IDPrefix.MINI_BOSS) == 0) {
                npcTeam.add(Mboss.createMBoss(
                        monsterInfo.monster.id,
                        monsterInfo.monster.level,
                        monsterInfo.monster.star,
                        monsterInfo.monster.kingdom,
                        monsterInfo.monster.element,
                        monsterInfo.monster.lethal));
            } else if (monsterInfo.monster.id.indexOf(IDPrefix.HERO) == 0) {
                npcTeam.add(Hero.createHero(
                        monsterInfo.monster.id,
                        monsterInfo.monster.level,
                        monsterInfo.monster.star,
                        monsterInfo.monster.lethal
                ));
            } else if (monsterInfo.monster.id.indexOf(IDPrefix.CREEP) == 0) {
                npcTeam.add(Creep.createCreep(
                        monsterInfo.monster.id,
                        monsterInfo.monster.level,
                        monsterInfo.monster.star,
                        monsterInfo.monster.kingdom,
                        monsterInfo.monster.element,
                        monsterInfo.monster.lethal
                ));
            }
        }
        props.put(Params.REMAINING_HP, Utils.toJson(remainingHP));
        props.put(Params.NPC_TEAM, Utils.toJson(npcTeam));

        //hero
        List<Hero> team = Hero.getPlayerTeam(uid, ETeamType.MONSTER_HUNT, huntHandler.getParentExtension().getParentZone(), false);
        HeroPackage heroPackage = new HeroPackage(team);
        props.put(Params.PLAYER_TEAM, Utils.toJson(heroPackage));
        cfg.setRoomProperties(props);

        //sage
        Sage sage = Sage.createMage(zone, uid);
        props.put(Params.SAGE, Utils.toJson(sage));

        //celestial
        Celestial celestial = Celestial.createCelestial(zone, uid);
        props.put(Params.CELESTIAL, Utils.toJson(celestial));

        cfg.setRoomProperties(props);

        List<RoomVariable> roomVariableList = new ArrayList<>();
        roomVariableList.add(new SFSRoomVariable(Params.TYPE, FightingType.PvM.getType()));
        roomVariableList.add(new SFSRoomVariable(Params.FUNCTION, EFightingFunction.HUNT.getIntValue()));
        cfg.setRoomVariables(roomVariableList);

        SendDoHunt objPut = new SendDoHunt();
        huntHandler.send(objPut, user);

        try {
            FightingCreater.creatorFightingRoom(zone, user, cfg);
        } catch (SFSCreateRoomException e) {
            e.printStackTrace();
        }

        //Event
        GameEventAPI.ariseGameEvent(EGameEvent.DO_HUNT, uid, new HashMap<>(), zone);
    }





    /*--------------------------------------------------  CONFIG  ----------------------------------------------------*/

    /**
     * get hunt config
     *
     * @return
     */
    public HuntConfig getHuntConfig() {
        return huntConfig;
    }

    public List<ResourcePackage> getCostFightHunt() {
        return getHuntConfig().readFightCost();
    }

    public RewardPowerVO getRewardHuntConfig(int avgLevel) {
        return rewardHuntConfig.readPowerConfig(avgLevel);
    }

    public List<ResourcePackage> getRewardWinHuntConfig() {
        return rewardHuntConfig.win;
    }

    public List<ResourcePackage> getRewardLoseHuntConfig() {
        return rewardHuntConfig.lose;
    }
}
