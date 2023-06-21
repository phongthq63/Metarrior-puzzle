package com.bamisu.log.gameserver.module.tower;

import com.bamisu.gamelib.entities.*;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.campaign.config.entities.MonsterOnTeam;
import com.bamisu.log.gameserver.module.characters.entities.*;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.mage.SageSkillModel;
import com.bamisu.log.gameserver.datamodel.tower.TowerManagerModel;
import com.bamisu.log.gameserver.datamodel.tower.UserTowerModel;
import com.bamisu.log.gameserver.datamodel.tower.entities.UserTowerInfo;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.campaign.entities.HeroPackage;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.friends.FriendHeroManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.bamisu.log.gameserver.module.hero.entities.HeroPosition;
import com.bamisu.log.gameserver.module.hero.exception.InvalidUpdateTeamException;
import com.bamisu.log.gameserver.module.ingame.FightingCreater;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingType;
import com.bamisu.log.gameserver.module.tower.cmd.send.SendFightTower;
import com.bamisu.log.gameserver.module.tower.config.RankTowerConfig;
import com.bamisu.log.gameserver.module.tower.config.TowerConfig;
import com.bamisu.log.gameserver.module.tower.config.entities.TowerVO;
import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;

import java.util.*;

public class TowerManager {
    private static TowerManager ourInstance = new TowerManager();

    public static TowerManager getInstance() {
        return ourInstance;
    }

    private TowerConfig towerConfig;
    private RankTowerConfig rankTowerConfig;




    private TowerManager() {
        //Loading config
        loadConfig();
    }

    private void loadConfig(){
        towerConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Tower.FILE_PATH_CONFIG_TOWER), TowerConfig.class);
        rankTowerConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Tower.FILE_PATH_CONFIG_RANK_TOWER), RankTowerConfig.class);
    }

    //Id cache
    private String getIdCache(Zone zone){
        return zone.getName();
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Get Model quan ly toan bo thap
     * @param zone
     * @return
     */
    public TowerManagerModel getTowerManagerModel(Zone zone){
        return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getTowerManagerModelCache();
    }

    /**
     * Get user Tower Model
     * @param uid
     * @param zone
     * @return
     */
    public UserTowerModel getUserTowerModel(long uid, Zone zone){
        UserTowerModel userTowerModel = UserTowerModel.copyFromDBtoObject(uid, zone);
        if(userTowerModel == null){
            userTowerModel = UserTowerModel.createUserTowerModel(uid, zone);
        }

        if (((ZoneExtension) zone.getExtension()).isTestServer()) {

        }

        return userTowerModel;
    }

    /**
     * Get ranker
     * @param zone
     * @return
     */
    public List<UserTowerInfo> getListTopRankTower(Zone zone){
        return getTowerManagerModel(zone).rank;
    }

    public boolean updateRankTowerManager(long uid, int floor, Zone zone){
        TowerManagerModel towerManagerModel = getTowerManagerModel(zone);
        if(towerManagerModel.updateRank(UserTowerInfo.create(uid, floor))){
            //Update cache
            towerManagerModel.executionRank();
        }
        return towerManagerModel.saveToDB(zone);
    }



    /**
     * Lay tang hien tai cua User Tower Model
     * @param uid
     * @param zone
     * @return
     */
    public int getFloorUserTowerModel(long uid, Zone zone){
        UserTowerModel userTowerModel = getUserTowerModel(uid, zone);
        return getFloorUserTowerModel(userTowerModel);
    }
    public int getFloorUserTowerModel(UserTowerModel userTowerModel){
        return userTowerModel.floor;
    }

    /**
     *
     * @param uid
     * @param zone
     * @return
     */
    public boolean upFloorUserTowerModel(long uid, Zone zone){
        UserTowerModel userTowerModel = getUserTowerModel(uid, zone);
        return upFloorUserTowerModel(userTowerModel, zone);
    }
    public boolean upFloorUserTowerModel(UserTowerModel userTowerModel, Zone zone){
        if(!updateRankTowerManager(userTowerModel.uid, userTowerModel.floor, zone))return false;
        userTowerModel.floor += 1;
        userTowerModel.timeStamp = Utils.getTimestampInSecond();

        //Event
        Map<String,Object> data = new HashMap<>();
        data.put(Params.FLOOR, userTowerModel.floor);
        GameEventAPI.ariseGameEvent(EGameEvent.FLOOR_TOWER_UPDATE, userTowerModel.uid, data, zone);

        //Save user model + update rank nguoi choi trong thap
        return userTowerModel.saveToDB(zone);
    }

    public void completeTower(Zone zone, Map<String, Object> statisticals, boolean isWin, long uid){
        TowerHandler towerHandler = ((TowerHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_TOWER));
        UserTowerModel userTowerModel = getUserTowerModel(uid, zone);

        if(isWin) {
            TowerVO towerCf = getTowerConfig(userTowerModel.floor);

            BagManager.getInstance().addItemToDB(towerCf.reward, uid, zone, UserUtils.TransactionType.COMPLETE_TOWER);
            upFloorUserTowerModel(userTowerModel, zone);

            if(HeroManager.getInstance().haveHeroFriendAssistantInTeam(uid, ETeamType.TOWER, zone)){
                //Update so lan co the muon hero
                FriendHeroManager.getInstance().updateCountAssignHeroFriend(uid, ETeamType.TOWER, isWin, zone);
                //Kiem tra xem co the muon khong
                if(!FriendHeroManager.getInstance().canAssignHeroFriend(uid, ETeamType.TOWER, zone)){
                    //Neu khong the tu clear khoi team
                    HeroManager.getInstance().clearHeroFriendTeamHero(uid, ETeamType.TOWER, zone);
                }
            }
        }else {
            //Luu tran thua lai
            userTowerModel.increaseLose(zone);
        }

        //Event
        Map<String, Object> data = statisticals;
        data.put(Params.WIN, isWin);
        data.put(Params.COUNT_LOSE, userTowerModel.readLose());
        data.putAll(statisticals);
        GameEventAPI.ariseGameEvent(EGameEvent.FINISH_TOWER_FIGHTING, uid, data, zone);
    }


    public void fight(User user, long uid, TowerHandler towerHandler, List<HeroPosition> update, TowerVO towerCf, Collection<String> sageSkill) {
        if(towerCf == null){
            SendFightTower objPut = new SendFightTower(ServerConstant.ErrorCode.ERR_MAX_FLOOR_TOWER);
            towerHandler.send(objPut, user);
            return;
        }

        //update team tower
        Zone zone = towerHandler.getParentExtension().getParentZone();
        try {
            HeroManager.getInstance().doUpdateTeamHero(zone, uid, ETeamType.TOWER.getId(), update);
        } catch (InvalidUpdateTeamException e) {
            e.printStackTrace();
            return;
        }

        //update sage skill
        SageSkillModel sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
        sageSkillModel.updateCurrentSkill(zone, sageSkill);

        //create tower fighting room
        CreateRoomSettings cfg = new CreateRoomSettings();
        cfg.setName(Utils.ranStr(10));
        cfg.setGroupId(ConfigHandle.instance().get(Params.TOWER_ROOM_GROUP_ID));
        cfg.setMaxUsers(1);
        cfg.setDynamic(true);
        cfg.setAutoRemoveMode(SFSRoomRemoveMode.WHEN_EMPTY);
        cfg.setGame(true);

        Map<Object, Object> props = new HashMap<>();
        props.put(Params.UID, uid);
        props.put(Params.WIN_CONDITIONS, towerCf.condition);
        props.put(Params.TOWER_INFO, towerCf);

        //Team NPC
        List<ICharacter> npcTeam = new ArrayList<>();
        for (MonsterOnTeam monsterOnTeam : towerCf.enemy) {
            if(monsterOnTeam.id.isEmpty()){ //slot trống
                npcTeam.add(null);
            }

            if (monsterOnTeam.id.indexOf(IDPrefix.MINI_BOSS) == 0) {
                npcTeam.add(Mboss.createMBoss(
                        monsterOnTeam.id,
                        monsterOnTeam.level,
                        monsterOnTeam.star,
                        monsterOnTeam.kingdom,
                        monsterOnTeam.element,
                        monsterOnTeam.lethal));
            } else if (monsterOnTeam.id.indexOf(IDPrefix.HERO) == 0) {
                npcTeam.add(Hero.createHero(
                        monsterOnTeam.id,
                        monsterOnTeam.level,
                        monsterOnTeam.star,
                        monsterOnTeam.lethal
                ));
            } else if (monsterOnTeam.id.indexOf(IDPrefix.CREEP) == 0) {
                npcTeam.add(Creep.createCreep(
                        monsterOnTeam.id,
                        monsterOnTeam.level,
                        monsterOnTeam.star,
                        monsterOnTeam.kingdom,
                        monsterOnTeam.element,
                        monsterOnTeam.lethal
                ));
            }
        }
        props.put(Params.NPC_TEAM, Utils.toJson(npcTeam));

        //hero
        List<Hero> team = Hero.getPlayerTeam(uid, ETeamType.TOWER, zone, false);
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
        roomVariableList.add(new SFSRoomVariable(Params.FUNCTION, EFightingFunction.TOWER.getIntValue()));
        cfg.setRoomVariables(roomVariableList);

        //Event
        GameEventAPI.ariseGameEvent(EGameEvent.DO_TOWER, uid, new HashMap<>(), zone);

        try {
            FightingCreater.creatorFightingRoom(zone, user, cfg);
        } catch (SFSCreateRoomException e) {
            e.printStackTrace();
        }
    }



    /*-----------------------------------------------------  CONFIG  -------------------------------------------------*/
    /**
     * Lay tower config
     * @return
     */
    public List<TowerVO> getTowerConfig(){
        return towerConfig.list;
    }
    public TowerVO getTowerConfig(int floor){
        for(TowerVO index : getTowerConfig()){
            if(index.floor == floor){
                return index;
            }
        }
        return null;
//        return towerConfig.list.get(floor - 1);
    }


    /**
     * Lay config rank tower
     * @return
     */
    public RankTowerConfig getRankTowerConfig(){
        return rankTowerConfig;
    }
}
