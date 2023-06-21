package com.bamisu.log.gameserver.module.mission;

import com.bamisu.gamelib.entities.*;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.datamodel.mission.UserMissionPuzzleModel;
import com.bamisu.log.gameserver.datamodel.mission.entities.MissionInfo;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.friends.FriendHeroManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.bamisu.log.gameserver.module.ingame.FightingCreater;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingType;
import com.bamisu.log.gameserver.module.mission.cmd.send.SendCompleteMission;
import com.bamisu.log.gameserver.module.mission.cmd.send.SendDoMission;
import com.bamisu.log.gameserver.module.mission.defind.EMissionAction;
import com.bamisu.log.gameserver.module.mission.defind.EMissionStatus;
import com.bamisu.log.gameserver.module.mission.config.*;
import com.bamisu.log.gameserver.module.mission.config.entities.*;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.sql.rank.dao.RankDAO;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;

import java.util.*;
import java.util.stream.Collectors;

public class MissionManager {

    private MissionConfig missionConfig;
    private MissionNameConfig missionNameConfig;
    private MissionAddConfig missionAddConfig;

    private Map<String, LIZRandom> missionRareInstance = new HashMap<>();
    private static MissionManager ourInstance = new MissionManager();

    public static MissionManager getInstance() {
        return ourInstance;
    }

    private MissionManager() {
        loadConfig();
    }

    private void loadConfig() {
        missionConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Mission.FILE_PATH_CONFIG_MISSION), MissionConfig.class);
        missionNameConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Mission.FILE_PATH_CONFIG_MISSION_NAME), MissionNameConfig.class);
        missionAddConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Mission.FILE_PATH_CONFIG_MISSION_ADD), MissionAddConfig.class);
    }


    private String getKeyMissionRare(int level) {
        return String.valueOf(level);
    }

    public LIZRandom getMissionRate(int level) {
        String keySummonRare = getKeyMissionRare(level);
        LIZRandom lizRandomGet = missionRareInstance.get(keySummonRare);
        if (lizRandomGet != null) return lizRandomGet;

        List<RandomObj> listRandomObj = new ArrayList<>();
        for (MissionVO cf : getMissionConfig()) {
            if (cf.unlock.point <= level) {
                listRandomObj.add(new RandomObj(cf.id, cf.rate));
            }
        }

        //ADD RARE
        LIZRandom rd = new LIZRandom();
        for (RandomObj index : listRandomObj) {
            rd.push(index);
        }

        //ADD SUMMON RARE INSTANCE
        missionRareInstance.put(keySummonRare, rd);
        return rd;
    }



    /* --------------------------------------------------------------------------------------------------------------*/
    /* --------------------------------------------------------------------------------------------------------------*/

    /**
     * Model quan ly nhiem vu nguoi choi
     *
     * @param uid
     * @param zone
     * @return
     */
    public UserMissionPuzzleModel getUserMissionModel(long uid, Zone zone) {
        UserMissionPuzzleModel userMissionModel = UserMissionPuzzleModel.copyFromDBtoObject(uid, zone);
        if (userMissionModel == null) {
            userMissionModel = UserMissionPuzzleModel.createUserMission(uid, zone);
        }
        return userMissionModel;
    }

    /**
     * Lay list nhiem vu
     *
     * @param uid
     * @param zone
     * @return
     */
    public List<MissionInfo> getListUserMissionInfo(long uid, Zone zone) {
        return getListUserMissionInfo(getUserMissionModel(uid, zone), zone);
    }
    public List<MissionInfo> getListUserMissionInfo(UserMissionPuzzleModel userMissionModel, Zone zone) {
        return userMissionModel.readMission(zone);
    }

    /**
     *
     * @param uid
     * @param zone
     * @return
     */
    public List<MissionInfo> getListUserMissionInfoCanFight(long uid, Zone zone) {
        return getListUserMissionInfoCanFight(getUserMissionModel(uid, zone), zone);
    }
    public List<MissionInfo> getListUserMissionInfoCanFight(UserMissionPuzzleModel userMissionModel, Zone zone) {
        return getListUserMissionInfo(userMissionModel, zone).stream().
                filter(obj -> !EMissionStatus.COMPLETED.getId().equals(obj.status)).
                collect(Collectors.toList());
    }

    /**
     *
     * @param uid
     * @param hash
     * @param zone
     * @return
     */
    public MissionInfo getUserMissionInfoCanComplete(long uid, String hash, Zone zone){
        UserMissionPuzzleModel userMissionModel = getUserMissionModel(uid, zone);
        return getUserMissionInfoCanComplete(userMissionModel, hash, zone);
    }
    public MissionInfo getUserMissionInfoCanComplete(UserMissionPuzzleModel userMissionModel, String hash, Zone zone){
        for (MissionInfo mission : getListUserMissionInfo(userMissionModel, zone)) {
            if (mission.hash.equals(hash)) {
                return mission;
            }
        }
        for(MissionInfo mission : userMissionModel.readMissionOld()){
            if (mission.hash.equals(hash)) {
                return mission;
            }
        }
        return null;
    }

    /**
     *
     * @param uid
     * @param hash
     * @param zone
     * @return
     */
    public MissionInfo getUserMissionInfo(long uid, String hash, Zone zone) {
        UserMissionPuzzleModel userMissionModel = getUserMissionModel(uid, zone);
        return getUserMissionInfo(userMissionModel, hash, zone);
    }
    public MissionInfo getUserMissionInfo(UserMissionPuzzleModel userMissionModel, String hash, Zone zone) {
        for (MissionInfo mission : getListUserMissionInfo(userMissionModel, zone)) {
            if (mission.hash.equals(hash)) {
                return mission;
            }
        }
        return null;
    }


    /**
     * Xu ly nhiem vu
     *
     * @param uid
     * @param hashMission
     * @param action
     * @param zone
     * @return
     */
    public boolean executionUserMission(long uid, String hashMission, EMissionAction action, Zone zone) {
        UserMissionPuzzleModel userMissionModel = getUserMissionModel(uid, zone);
        return executionUserMission(userMissionModel, hashMission, action, zone);
    }

    public boolean executionUserMission(UserMissionPuzzleModel userMissionModel, String hashMission, EMissionAction action, Zone zone) {
        return userMissionModel.executionMission(hashMission, action, zone);
    }

    /**
     * Update status mission
     *
     * @param uid
     * @param hashMission
     * @param status
     * @param zone
     * @return
     */
    public boolean updateStatusUserMission(long uid, String hashMission, EMissionStatus status, boolean isWin, Zone zone) {
        UserMissionPuzzleModel userMissionModel = getUserMissionModel(uid, zone);
        return updateStatusUserMission(userMissionModel, hashMission, status, isWin, zone);
    }

    public boolean updateStatusUserMission(UserMissionPuzzleModel userMissionModel, String hashMission, EMissionStatus status, boolean isWin, Zone zone) {
        return userMissionModel.updateStatusMision(hashMission, status, isWin, zone);
    }

    /**
     * Thuc hien nhiem vu
     *
     * @param zone
     * @param statisticals
     * @param isWin
     * @param uid
     * @param hashMission
     */
    public void complateMission(Zone zone, Map<String, Object> statisticals, boolean isWin, long uid, String hashMission, long point) {
        MissionHandler missionHandler = (MissionHandler) ((ZoneExtension) zone.getExtension()).getServerHandler(Params.Module.MODULE_MISSION);
        User user = ExtensionUtility.getInstance().getUserById(uid);
        UserMissionPuzzleModel userMissionModel = getUserMissionModel(uid, zone);

        if (isWin) {
//            MissionInfo missionInfo = getUserMissionInfoCanComplete(userMissionModel, hashMission, zone);
//            if(missionInfo == null){
//                SendCompleteMission objPut = new SendCompleteMission(ServerConstant.ErrorCode.ERR_INVALID_MISSION);
//                missionHandler.send(objPut, user);
//                return;
//            }

            //Them so tran thang mission
            userMissionModel.winMission++;
            //Update status
//            if (!updateStatusUserMission(userMissionModel, hashMission, EMissionStatus.COMPLETED, isWin, zone)) {
//                SendCompleteMission objPut = new SendCompleteMission(ServerConstant.ErrorCode.ERR_SYS);
//                missionHandler.send(objPut, user);
//                return;
//            }

            // create vao da lam xong mission va Them phan thuong vao tui
//            if (!BagManager.getInstance().addItemToDB(missionInfo.readReward(), uid, zone, UserUtils.TransactionType.COMPLETE_MISSION)) {
//                SendCompleteMission objPut = new SendCompleteMission(ServerConstant.ErrorCode.ERR_SYS);
//                missionHandler.send(objPut, user);
//                return;
//            }

            SendCompleteMission objPut = new SendCompleteMission();
//            objPut.hash = missionInfo.hash;
            missionHandler.send(objPut, ExtensionUtility.getInstance().getUserById(uid));

            //Kiem tra xem co hero
            if (HeroManager.getInstance().haveHeroFriendAssistantInTeam(uid, ETeamType.MISSION_OUTPOST, zone)) {
                //Update so lan co the muon hero
                FriendHeroManager.getInstance().updateCountAssignHeroFriend(uid, ETeamType.MISSION_OUTPOST, isWin, zone);
                //Kiem tra xem co the muon khong
                if (!FriendHeroManager.getInstance().canAssignHeroFriend(uid, ETeamType.MISSION_OUTPOST, zone)) {
                    //Neu khong the tu clear khoi team
                    HeroManager.getInstance().clearHeroFriendTeamHero(uid, ETeamType.MISSION_OUTPOST, zone);
                }
            }
        } else {
            //Luu so tran thua
            userMissionModel.increaseLose(zone);
        }

        //Update leaderboard
        if (TimeUtils.getDeltaTimeToTime(ETimeType.NEW_WEEK, Utils.getTimestampInSecond()) - 86400 > 0) {
            RankDAO.updateRankMission(zone, uid, (int) point);
        }

        //Event
        Map<String, Object> data = new HashMap<>();
        data.put(Params.WIN, isWin);
        data.put(Params.COUNT_WIN, userMissionModel.winMission);
        data.put(Params.COUNT_LOSE, userMissionModel.readLose());
        data.putAll(statisticals);
        GameEventAPI.ariseGameEvent(EGameEvent.FINISH_MISSION_FIGHTING, uid, data, zone);
    }


    /**
     * @param missionHandler
     * @param user
     */
    public void doMission(MissionHandler missionHandler, User user) {
        ZoneExtension extension = ((ZoneExtension) missionHandler.getParentExtension());
        UserModel userModel = extension.getUserManager().getUserModel(user);
        long uid = userModel.userID;
        Zone zone = missionHandler.getParentExtension().getParentZone();

//        UserMissionPuzzleModel userMissionModel = getUserMissionModel(uid, zone);
//        MissionInfo missionInfo = getUserMissionInfo(userMissionModel, missionHash, extension.getParentZone());
//        //Kiem tra config
//        if (missionInfo == null || !missionInfo.status.equalsIgnoreCase(EMissionStatus.DOING.getId())) {
//            SendDoMission objPut = new SendDoMission(ServerConstant.ErrorCode.ERR_INVALID_MISSION);
//            missionHandler.send(objPut, user);
//            return;
//        }
        if (TimeUtils.getDeltaTimeToTime(ETimeType.NEW_WEEK, Utils.getTimestampInSecond()) - 86400 <= 0) {
            SendDoMission objPut = new SendDoMission(ServerConstant.ErrorCode.ERR_INVALID_MISSION);
            missionHandler.send(objPut, user);
            return;
        }

        //Kiem tra tieu tai nguyen
        if(!BagManager.getInstance().addItemToDB(
                getCostFightMission(),
                uid,
                zone,
                UserUtils.TransactionType.DO_MISSION)){
            SendDoMission objPut = new SendDoMission(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
            missionHandler.send(objPut, user);
            return;
        }

//        //Xu ly mission
//        if (!executionUserMission(userMissionModel, missionHash, EMissionAction.DO, zone)) {
//            SendDoMission objPut = new SendDoMission(ServerConstant.ErrorCode.ERR_SYS);
//            missionHandler.send(objPut, user);
//            return;
//        }

        CreateRoomSettings cfg = new CreateRoomSettings();
        cfg.setName("mission_" + uid + "_" + Utils.ranStr(10));
        cfg.setGroupId("mission");
        cfg.setMaxUsers(1);
        cfg.setDynamic(true);
        cfg.setAutoRemoveMode(SFSRoomRemoveMode.NEVER_REMOVE);
        cfg.setGame(true);

        Map<Object, Object> props = new HashMap<>();
        props.put(Params.UID, uid);
//        props.put(Params.MISSION_INFO, missionInfo);

        cfg.setRoomProperties(props);

        List<RoomVariable> roomVariableList = new ArrayList<>();
        roomVariableList.add(new SFSRoomVariable(Params.TYPE, FightingType.PvM.getType()));
        roomVariableList.add(new SFSRoomVariable(Params.FUNCTION, EFightingFunction.MISSION.getIntValue()));
        cfg.setRoomVariables(roomVariableList);

        //Event
        GameEventAPI.ariseGameEvent(EGameEvent.DO_MISSION, userModel.userID, new HashMap<>(), zone);

        try {
            FightingCreater.creatorFightingRoom(zone, user, cfg);
        } catch (SFSCreateRoomException e) {
            e.printStackTrace();
        }
    }


    /*-------------------------------------------------- CONFIG ------------------------------------------------------*/

    /**
     * Get mission config
     *
     * @return
     */
    public List<MissionVO> getMissionConfig() {
        return missionConfig.listMission;
    }

    public MissionVO getMissionConfig(String idMission) {
        return missionConfig.readMission(idMission);
    }

    public List<String> getMissionNameConfig() {
        return missionNameConfig.list;
    }

    public List<ResourcePackage> getCostFightMission(){
        return missionConfig.readCostFight();
    }

    public int getCountRedoMission(){
        return missionConfig.redo;
    }

    public MissionAddConfig getMissionAddConfig(){
        return missionAddConfig;
    }
}
