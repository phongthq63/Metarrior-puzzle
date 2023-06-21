package com.bamisu.log.gameserver.module.campaign;

import com.bamisu.gamelib.entities.IDPrefix;
import com.bamisu.gamelib.sql.game.dbo.RankLeagueDBO;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.log.gameserver.datamodel.campaign.UserCampaignRankModel;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.campaign.config.entities.MonsterOnTeam;
import com.bamisu.log.gameserver.module.characters.entities.*;
import com.bamisu.log.gameserver.module.friends.FriendHeroManager;
import com.bamisu.log.gameserver.module.hero.exception.InvalidUpdateTeamException;
import com.bamisu.log.gameserver.module.league.LeagueManager;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateVip;
import com.bamisu.log.gameserver.sql.rank.dao.RankDAO;
import com.google.common.collect.Lists;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.campaign.UserCampaignDetailModel;
import com.bamisu.log.gameserver.datamodel.campaign.entities.StoreSlotCampaignInfo;
import com.bamisu.log.gameserver.datamodel.mage.SageSkillModel;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.adventure.AdventureManager;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.campaign.cmd.rec.RecFightMainCampaign;
import com.bamisu.log.gameserver.module.campaign.cmd.send.*;
import com.bamisu.log.gameserver.module.campaign.config.BonusCampaignConfig;
import com.bamisu.log.gameserver.module.campaign.config.StoreCampaignSlotConfig;
import com.bamisu.log.gameserver.module.campaign.config.entities.Station;
import com.bamisu.log.gameserver.module.campaign.config.entities.Area;
import com.bamisu.log.gameserver.module.campaign.config.MainCampaignConfig;
import com.bamisu.log.gameserver.module.campaign.entities.HeroPackage;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.bamisu.log.gameserver.module.hero.entities.HeroPosition;
import com.bamisu.log.gameserver.module.ingame.FightingCreater;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingType;
import com.bamisu.gamelib.item.entities.MoneyPackageVO;
import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Create by Popeye on 10:31 AM, 2/6/2020
 */
public class CampaignManager {
    private Logger logger = Logger.getLogger("arena");
    public static CampaignManager instance;
    public Map<Long, ISFSObject> mapSetCampaign = new HashMap<>();

    private ScheduledExecutorService scheduledExecutor;

    public static CampaignManager getInstance() {
        if (instance == null) {
            instance = new CampaignManager();
        }

        return instance;
    }

    private CampaignManager() {
        scheduledExecutor = LizThreadManager.getInstance().getFixExecutorServiceByName(LizThreadManager.EThreadPool.CAMPAIGN, 1);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Lay campaign model
     *
     * @param zone
     * @param uid
     * @return
     */
    public UserCampaignDetailModel getUserCampaignDetailModel(Zone zone, long uid) {
        return UserCampaignDetailModel.copyFromDBtoObject(uid, zone);
    }

    public UserCampaignDetailModel getUserCampaignDetailModel(Zone zone, String uid) {
        return UserCampaignDetailModel.copyFromDBtoObject(uid, zone);
    }


    /**
     * Lay sao cua station
     *
     * @param zone
     * @param uid
     * @param area
     * @param station
     * @return
     */
    public int getStarUserCampaignModel(Zone zone, long uid, int area, int station) {
        UserCampaignDetailModel userCampaignDetailModel = getUserCampaignDetailModel(zone, uid);
        return getStarUserCampaignModel(userCampaignDetailModel, area, station);
    }

    public int getStarUserCampaignModel(UserCampaignDetailModel userCampaignDetailModel, int area, int station) {
        return userCampaignDetailModel.userMainCampaignDetail.readStarStation(area, station);
    }

    /**
     * Kiem tra campaign co the danh dc khong
     *
     * @param uid
     * @param station
     * @param zone
     * @return
     */
    public boolean canFightCampaign(long uid, int station, Zone zone) {
        UserCampaignDetailModel userCampaignDetailModel = getUserCampaignDetailModel(zone, uid);
        return userCampaignDetailModel.userMainCampaignDetail.canFightStation(station);
    }

    public void completeCampaign(Zone zone, Map<String, Object> statisticals, boolean isWin, long uid, int station, int star) {
        completeCampaign(zone, statisticals, isWin, uid, station, star, false);
    }

    /**
     * Hoan thanh campaign
     *
     * @param zone
     * @param statisticals
     * @param uid
     * @param station
     * @param star
     */
    public void completeCampaign(Zone zone, Map<String, Object> statisticals, boolean isWin, long uid, int station, int star, boolean isTutorial) {
        UserCampaignDetailModel userCampaignDetailModel = getUserCampaignDetailModel(zone, uid);
        UserModel um = ((ZoneExtension) zone.getExtension()).getUserManager().getUserModel(uid);

        //Dem so count fight
        UserCampaignRankModel.copyFromDBtoObject(uid, zone).updateCountFight(zone);

        if (isWin) {
            int currentArea = Arrays.asList(userCampaignDetailModel.userMainCampaignDetail.readNextStation().split(",", 2)).parallelStream().mapToInt(Integer::parseInt).toArray()[0];
            MainCampaignConfig campaignConfig = MainCampaignConfig.getInstance();

            //Phan thuong hoan thanh station
            completeStateCampaign(zone, userCampaignDetailModel, currentArea, station, star, campaignConfig);

            //Update AFK package
            User user = ExtensionUtility.getInstance().getUserById(uid);
            AdventureManager.getInstance().newCampaign(um, user, userCampaignDetailModel.userMainCampaignDetail, isTutorial);

            if (HeroManager.getInstance().haveHeroFriendAssistantInTeam(uid, ETeamType.CAMPAIGN, zone)) {
                //Update so lan co the muon hero
                FriendHeroManager.getInstance().updateCountAssignHeroFriend(uid, ETeamType.CAMPAIGN, isWin, zone);
                //Kiem tra xem co the muon khong
                if (!FriendHeroManager.getInstance().canAssignHeroFriend(uid, ETeamType.CAMPAIGN, zone)) {
                    //Neu khong the tu clear khoi team
                    HeroManager.getInstance().clearHeroFriendTeamHero(uid, ETeamType.CAMPAIGN, zone);
                }
            }
        } else {
            //Thua luu lai so tran
            userCampaignDetailModel.increaseLose(zone);
        }

        //Event
        Map<String, Object> data = new HashMap<>();
        data.put(Params.WIN, isWin);
        data.put(Params.COUNT_LOSE, userCampaignDetailModel.readLose());
        data.putAll(statisticals);
        GameEventAPI.ariseGameEvent(EGameEvent.FINISH_CAMPAIGN_FIGHTING, uid, data, zone);
    }

    private void completeStateCampaign(Zone zone, UserCampaignDetailModel userCampaignDetailModel, int area, int station, int star, MainCampaignConfig campaignConfig) {
        ZoneExtension extension = (ZoneExtension) zone.getExtension();
        CampaignHandler handler = (CampaignHandler) extension.getServerHandler(Params.Module.MODULE_CAMPAIGN);
        int oldStar = userCampaignDetailModel.userMainCampaignDetail.readStarStation(area, station);

        //Update station (thanh cong --- nhan qua / that bai --- khong nhan j ca)
        if (userCampaignDetailModel.userMainCampaignDetail.updateState(station, star)) {
            //Save data
            userCampaignDetailModel.saveToDB(zone);

            //Event
            Map<String, Object> data = new HashMap<>();
            data.put(Params.AREA, area);
            data.put(Params.STATION, station);
            GameEventAPI.ariseGameEvent(EGameEvent.STATION_CAMPAIGN_UPDATE, userCampaignDetailModel.uid, data, zone);
        } else {
            SendCompleteCampaign objPut = new SendCompleteCampaign();
            handler.send(objPut, ExtensionUtility.getInstance().getUserById(userCampaignDetailModel.uid));
            return;
        }

        //Lay config
        Station stationCf = campaignConfig.getArea(area).readStation(station);
        List<ResourcePackage> resource = new ArrayList<>();
        //Nhan sao
        resource.add(new MoneyPackageVO(MoneyType.STAR_CAMPAIGN.getId(), star - oldStar));
        //Thuong lan dau
        if (oldStar == 0) {
            resource.addAll(BonusCampaignConfig.getInstance().getBonusStation(area, station));
            if (TimeUtils.getDeltaTimeToTime(ETimeType.NEW_WEEK, Utils.getTimestampInSecond()) - 86400 > 0 && area > 0) {
                int score = 0;
                switch (area + 1) {
                    case 1:
                        score = 10;
                        break;
                    case 2:
                        score = 20;
                        break;
                    case 3:
                        score = 30;
                        break;
                    case 4:
                        score = 50;
                        break;
                    case 5:
                        score = 80;
                        break;
                    case 6:
                        score = 130;
                        break;
                    case 7:
                        score = 210;
                        break;
                    case 8:
                        score = 340;
                        break;
                    case 9:
                        score = 550;
                        break;
                    case 10:
                        score = 890;
                        break;
                    case 11:
                        score = 1440;
                        break;
                    case 12:
                        score = 2330;
                        break;
                    case 13:
                        score = 3770;
                        break;
                    case 14:
                        score = 6110;
                        break;
                    case 15:
                        score = 9870;
                        break;
                }
//                RankDAO.updateRankCampaign(zone, userCampaignDetailModel.uid, score);
                ((ZoneExtension) zone.getExtension()).trace("Update score campaign - Start - " + Utils.toJson(userCampaignDetailModel) + " " + score);
                LeagueManager.getInstance().updateRankCampaign(zone, userCampaignDetailModel.uid, score);
                ((ZoneExtension) zone.getExtension()).trace("Update score campaign - End");
            }
        }
        //Thuong man
        resource.addAll(stationCf.readRewardComplete(oldStar, star));
        //Ad do vao tui
        if (!BagManager.getInstance().addItemToDB(resource, userCampaignDetailModel.uid, zone, UserUtils.TransactionType.COMPLETE_CAMPAIGN)) {
            SendCompleteCampaign objPut = new SendCompleteCampaign(ServerConstant.ErrorCode.ERR_SYS);
            handler.send(objPut, ExtensionUtility.getInstance().getUserById(userCampaignDetailModel.uid));
            return;
        }

        SendCompleteCampaign objPut = new SendCompleteCampaign();
        handler.send(objPut, ExtensionUtility.getInstance().getUserById(userCampaignDetailModel.uid));
    }


    public void updateAreaCampaign(Zone zone, User user) {
        ZoneExtension extension = (ZoneExtension) zone.getExtension();
        CampaignHandler handler = (CampaignHandler) extension.getServerHandler(Params.Module.MODULE_CAMPAIGN);
        UserCampaignDetailModel userCampaignDetailModel = getUserCampaignDetailModel(zone, user.getName());
        if (!userCampaignDetailModel.userMainCampaignDetail.updateArea()) {
            SendUpdateAreaCampaign objPut = new SendUpdateAreaCampaign(ServerConstant.ErrorCode.ERR_CURRENT_CANT_UPDATE_AREA_CAMPAIGN);
            handler.send(objPut, user);
            return;
        } else {

            //Event
            Map<String, Object> data = new HashMap<>();
            data.put(Params.AREA, Integer.parseInt(userCampaignDetailModel.userMainCampaignDetail.readNextStation().split(",")[0]));
            GameEventAPI.ariseGameEvent(EGameEvent.CHAP_CAMPAIGN_UPDATE, userCampaignDetailModel.uid, data, zone);

            userCampaignDetailModel.saveToDB(zone);
        }

        SendUpdateAreaCampaign objPut = new SendUpdateAreaCampaign();
        handler.send(objPut, user);
    }

    /**
     * Doc store campaign
     *
     * @param zone
     * @return
     */
    public List<StoreSlotCampaignInfo> readStoreCampaign(Zone zone, UserCampaignDetailModel userCampaignDetailModel) {
        List<StoreSlotCampaignInfo> list = new ArrayList<>();
        boolean flat = false;
        for (StoreSlotCampaignInfo data : userCampaignDetailModel.userStoreCampaignInfo.slots) {
            if (!data.haveLock()) {
                //Neu khong co item -> gen item
                //Neu co item -> refresh lai neu can
                if (data.refresh()) {
                    if (data.genReward(Integer.parseInt(userCampaignDetailModel.userMainCampaignDetail.readNextStation().split(",", 2)[0]))) {
                        flat = true;
                    }
                }

                list.add(data);
            }
        }
        //Neu co thay doi create vao database
        if (flat) {
            userCampaignDetailModel.saveToDB(zone);
        }

        return list;
    }


    public void buyStoreCampaign(Zone zone, User user, int slot) {
        ZoneExtension extension = (ZoneExtension) zone.getExtension();
        CampaignHandler handler = (CampaignHandler) extension.getServerHandler(Params.Module.MODULE_CAMPAIGN);
        long uid = Long.parseLong(user.getName());
        UserCampaignDetailModel userCampaignDetailModel = getUserCampaignDetailModel(zone, uid);

        //Hien chua co kb gioi han mua + time refresh nen chua viet check dk


        for (StoreSlotCampaignInfo data : userCampaignDetailModel.userStoreCampaignInfo.slots) {
            data.refresh();

            if (data.position == slot && !data.haveLock() && data.reward != null) {
                //Tieu tai nguyen
                if (!BagManager.getInstance().addItemToDB(
                        Lists.newArrayList(StoreCampaignSlotConfig.getInstance().store.get(data.position - 1).cost).parallelStream().map(obj -> new MoneyPackageVO(obj.id, -obj.amount)).collect(Collectors.toList()),
                        uid,
                        zone, UserUtils.TransactionType.BUY_CAMPAIGN_STORE)) {

                    SendBuyStoreCampaign send = new SendBuyStoreCampaign(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_RESOURCE);
                    handler.send(send, user);
                    return;
                }

                //Mua thanh cong
                data.buy++;
                List<ResourcePackage> reward = new ArrayList<>(Collections.singleton(data.reward));
                if (BagManager.getInstance().addItemToDB(reward, uid, zone, UserUtils.TransactionType.BUY_CAMPAIGN_STORE) && userCampaignDetailModel.saveToDB(zone)) {
                    SendBuyStoreCampaign send = new SendBuyStoreCampaign();
                    send.reward = data.reward;
                    handler.send(send, user);
                    return;
                }

                SendBuyStoreCampaign send = new SendBuyStoreCampaign(ServerConstant.ErrorCode.ERR_SYS);
                handler.send(send, user);
                return;
            }
        }

        SendBuyStoreCampaign send = new SendBuyStoreCampaign(ServerConstant.ErrorCode.ERR_INVALID_BUY_ITEM_CAMPAIGN);
        handler.send(send, user);
    }


    /**
     * Đánh phó bản chính
     *
     * @param user
     * @param recFightMainCampaign
     */
    public void fightMainCampaign(Zone zone, User user, RecFightMainCampaign recFightMainCampaign, List<HeroPosition> update, Collection<String> updateSageSkill) {
        ZoneExtension extension = (ZoneExtension) zone.getExtension();
        CampaignHandler handler = (CampaignHandler) extension.getServerHandler(Params.Module.MODULE_CAMPAIGN);
        UserModel userModel = extension.getUserManager().getUserModel(user);
        if (!canFightCampaign(userModel.userID, recFightMainCampaign.station, zone)) {
            SendFightCampaign objPut = new SendFightCampaign(ServerConstant.ErrorCode.ERR_INVALID_FIGHT_STATION_CAMPAIGN);
            handler.send(objPut, user);
            return;
        }

        String[] fightingServer = extension.getConfigProperties().getProperty("fighting_server").split(",");
        String serverAddr = fightingServer[Utils.randomInRange(0, fightingServer.length - 1)];
        if (serverAddr.equalsIgnoreCase("local")) {
            createFightingRoomMainCampaign(zone, serverAddr, userModel.userID, userModel, recFightMainCampaign.area, recFightMainCampaign.station, update, updateSageSkill, recFightMainCampaign.isTutorial);
        } else {

        }
    }

    public void createFightingRoomMainCampaign(Zone zone, String source, long uid, UserModel um, int areaID, int stationID, List<HeroPosition> update, Collection<String> updateSageSkill, boolean isTutorial) {
        Area area = MainCampaignConfig.getInstance().getArea(areaID);
        Station stationConfig = area.readStation(stationID);
        Station station = null;

        if(mapSetCampaign.containsKey(uid)){
            Station tmpStation = Utils.fromJson(mapSetCampaign.get(uid).toJson(), Station.class);
            tmpStation.reward = stationConfig.reward;
            tmpStation.complete = stationConfig.complete;
            tmpStation.number = stationConfig.number;
            tmpStation.name = stationConfig.name;
            tmpStation.condition = stationConfig.condition;
            tmpStation.bbg = stationConfig.bbg;
            station = tmpStation;
            mapSetCampaign.remove(uid);
        }else {
            station = stationConfig;
        }
        ZoneExtension zoneExtension = (ZoneExtension) zone.getExtension();

        User user = ExtensionUtility.getInstance().getUserById(uid);
        if (user == null) return;

        //update hero team
        List<HeroPosition> updateCache = new ArrayList<>(update);
        try {
            HeroManager.getInstance().doUpdateTeamHero(zone, uid, ETeamType.CAMPAIGN.getId(), update);
        } catch (InvalidUpdateTeamException e) {
            e.printStackTrace();
            return;
        }

        List<Hero> pvTeam = Hero.getPlayerTeam(uid, ETeamType.PVP_OFFLINE, zone, false);
        boolean isEmpty = true;
        for (Hero hero : pvTeam) {
            if (hero != null) isEmpty = false;
            break;
        }
        if (isEmpty) {
            try {
                HeroManager.getInstance().doUpdateTeamHero(zone, uid, ETeamType.PVP_OFFLINE.getId(), updateCache);
            } catch (InvalidUpdateTeamException e) {
                e.printStackTrace();
                return;
            }
        }

        //update sage skill
        SageSkillModel sageSkillModel = SageSkillModel.copyFromDBtoObject(uid, zone);
        sageSkillModel.updateCurrentSkill(zone, updateSageSkill);
        if(isTutorial){
            if (areaID == 0 & stationID == 0) {   //đánh của đầu tiên
                try {
                    if (SDKGateVip.canTakeFeeVip(um.accountID)) { //có tutorial
                        //Event
                        GameEventAPI.ariseGameEvent(EGameEvent.DO_CAMPAIGN, uid, new HashMap<>(), zone);
                        return;
                    }
                } catch (TException e) {
                    e.printStackTrace();
                }
            }
        }

        CreateRoomSettings cfg = new CreateRoomSettings();
        cfg.setName(Utils.ranStr(10));
        cfg.setGroupId(zoneExtension.getServerID() + ":" + ConfigHandle.instance().get(Params.CAMPAIGN_ROOM_GROUP_ID));
        cfg.setMaxUsers(1);
        cfg.setDynamic(true);
        cfg.setAutoRemoveMode(SFSRoomRemoveMode.WHEN_EMPTY);
        cfg.setGame(true);

        Map<Object, Object> props = new HashMap<>();
        props.put(Params.AREA, Utils.toJson(area));
        props.put(Params.STATION, Utils.toJson(station));
        props.put(Params.AREA_ID, areaID);
        props.put(Params.STATION_ID, stationID);
        props.put(Params.OLD_STAR, CampaignManager.getInstance().getStarUserCampaignModel(zone, uid, areaID, stationID));

        //uid, wincondition
        props.put(Params.UID, uid);
        props.put(Params.WIN_CONDITIONS, station.condition);

        //Team NPC
        List<ICharacter> npcTeam = new ArrayList<>();
        for (MonsterOnTeam monsterOnTeam : station.enemy) {
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
        List<Hero> team = Hero.getPlayerTeam(uid, ETeamType.CAMPAIGN, zone, false);
        HeroPackage heroPackage = new HeroPackage(team);
        props.put(Params.PLAYER_TEAM, Utils.toJson(heroPackage));

        //sage
        Sage sage = Sage.createMage(zone, uid);
        props.put(Params.SAGE, Utils.toJson(sage));

        //celestial
        Celestial celestial = Celestial.createCelestial(zone, uid);
        props.put(Params.CELESTIAL, Utils.toJson(celestial));

        props.put(Params.TUTORIAL_STATE, um.stageV3);

        cfg.setRoomProperties(props);

        List<RoomVariable> roomVariableList = new ArrayList<>();
        roomVariableList.add(new SFSRoomVariable(Params.TYPE, FightingType.PvM.getType()));
        roomVariableList.add(new SFSRoomVariable(Params.FUNCTION, EFightingFunction.CAMPAIGN.getIntValue()));
        cfg.setRoomVariables(roomVariableList);

        try {
            FightingCreater.creatorFightingRoom(zone, user, cfg);
        } catch (SFSCreateRoomException e) {
            e.printStackTrace();
        }

        //Event
        GameEventAPI.ariseGameEvent(EGameEvent.DO_CAMPAIGN, uid, new HashMap<>(), zone);
    }

    public void setCampaign(ISFSObject rec) {
        mapSetCampaign.put(Long.valueOf(rec.getUtfString("uid")), rec);
        System.out.println("set ok");
        System.out.println(rec.toJson());
    }
}
