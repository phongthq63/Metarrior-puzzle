package com.bamisu.log.gameserver.module.IAPBuy;

import com.bamisu.log.gameserver.datamodel.campaign.UserCampaignDetailModel;
import com.bamisu.log.gameserver.datamodel.hunt.UserHuntModel;
import com.bamisu.log.gameserver.datamodel.mission.UserMissionPuzzleModel;
import com.bamisu.log.gameserver.datamodel.tower.UserTowerModel;
import com.bamisu.log.gameserver.entities.ColorHero;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EConditionType;
import com.bamisu.log.gameserver.module.GameEvent.BaseGameEvent;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.campaign.CampaignManager;
import com.bamisu.log.gameserver.module.campaign.config.MainCampaignConfig;
import com.bamisu.log.gameserver.module.campaign.config.entities.Area;
import com.bamisu.log.gameserver.module.hunt.HuntManager;
import com.bamisu.log.gameserver.module.mission.MissionManager;
import com.bamisu.log.gameserver.module.tower.TowerManager;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IAPGameEventHandle extends BaseGameEvent {

    public IAPGameEventHandle(Zone zone) {
        super(zone);
    }

    @Override
    public void handleGameEvent(EGameEvent event, long uid, Map<String,Object> data) {
        switch (event){
            case USER_PAYMENT:

                break;
            case LEVEL_USER_UPDATE:
                handerLevelUserUpdate(uid, data);
                break;
            case STAR_HERO_UPDATE:
                handerStarHeroUpdate(uid, data);
                break;
            case CHAP_CAMPAIGN_UPDATE:
                handerChapCampaignUpdate(uid, data);
                break;
            case STATION_CAMPAIGN_UPDATE:
                handerStationCampaignUpdate(uid, data);
                break;
            case FLOOR_TOWER_UPDATE:
                handerFloorTowerUpdate(uid, data);
                break;
            case GET_HERO:
                handerGetHero(uid, data);
                break;
            case FINISH_HUNT_FIGHTING:
                handerFinishHunt(uid, data);
                break;
            case FINISH_MISSION_FIGHTING:
                handerFinishMission(uid, data);
                break;
            case FINISH_CAMPAIGN_FIGHTING:
                handerFinishCampaign(uid, data);
                break;
            case FINISH_TOWER_FIGHTING:
                handerFinishTower(uid, data);
                break;
        }
    }

    @Override
    public void initEvent() {
        this.registerEvent(EGameEvent.USER_PAYMENT);
        this.registerEvent(EGameEvent.LEVEL_USER_UPDATE);
        this.registerEvent(EGameEvent.CHAP_CAMPAIGN_UPDATE);
        this.registerEvent(EGameEvent.STATION_CAMPAIGN_UPDATE);
        this.registerEvent(EGameEvent.FLOOR_TOWER_UPDATE);
        this.registerEvent(EGameEvent.STAR_HERO_UPDATE);
        this.registerEvent(EGameEvent.GET_HERO);
        this.registerEvent(EGameEvent.FINISH_HUNT_FIGHTING);
        this.registerEvent(EGameEvent.FINISH_MISSION_FIGHTING);
        this.registerEvent(EGameEvent.FINISH_CAMPAIGN_FIGHTING);
        this.registerEvent(EGameEvent.FINISH_TOWER_FIGHTING);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    private void handerLevelUserUpdate(long uid, Map<String,Object> data){
        int level = (short) data.getOrDefault(Params.LEVEL, -1);
        if(level == -1)return;

        IAPBuyManager.getInstance().triggerExsistPackageIAPModel(uid, EConditionType.LEVEL_USER, level, zone);
    }


    private  void handerStarHeroUpdate(long uid, Map<String,Object> data){
        List<Short> star = (List<Short>) data.getOrDefault(Params.STAR, new ArrayList<>());
        if(star.isEmpty())return;

        if(star.contains(ColorHero.getStarFromName(EConditionType.GET_MYTHIC_HERO.getDescription()))){
            IAPBuyManager.getInstance().triggerExsistPackageIAPModel(uid, EConditionType.GET_MYTHIC_HERO, 1, zone);
        }else if(star.contains(ColorHero.getStarFromName(EConditionType.GET_ASCENDED_HERO.getDescription()))){
            IAPBuyManager.getInstance().triggerExsistPackageIAPModel(uid, EConditionType.GET_ASCENDED_HERO, 1, zone);
        }
    }

    private void handerChapCampaignUpdate(long uid, Map<String,Object> data){
        int area = (int) data.getOrDefault(Params.AREA, -1);
        if(area < 0)return;

        IAPBuyManager.getInstance().triggerExsistPackageIAPModel(uid, EConditionType.CHAP_DUNGEON, area, zone);
        IAPBuyManager.getInstance().increaseIAPChallenge(uid, "prestige3", EConditionType.CHAP_DUNGEON, area, zone);
    }

    private void handerStationCampaignUpdate(long uid, Map<String,Object> data){
        int area = (int) data.getOrDefault(Params.AREA, -1);
        int station = (int) data.getOrDefault(Params.STATION, -1);
        if(area < 0 || station < 0)return;

        //station start = 0, area start = 0
        int stationCount = station + 1;
        List<Area> listAreaCf = MainCampaignConfig.getInstance().area;
        for(int i = 0; i < listAreaCf.size(); i++){
            if(i >= area) break;
            stationCount += listAreaCf.get(i).station.size();
        }

        IAPBuyManager.getInstance().triggerExsistPackageIAPModel(uid, EConditionType.STATION_DUNGEON, stationCount, zone);
    }


    private void handerFloorTowerUpdate(long uid, Map<String,Object> data){
        int floor = (short) data.getOrDefault(Params.FLOOR, -1);
        if(floor < 0)return;

        IAPBuyManager.getInstance().triggerExsistPackageIAPModel(uid, EConditionType.FLOOR_TOWER, floor - 1, zone);
    }


    private void handerGetHero(long uid, Map<String,Object> data){
        List<Short> star = (List<Short>) data.getOrDefault(Params.STAR, new ArrayList<>());
        if(star.isEmpty())return;

        if(star.contains(ColorHero.getStarFromName(EConditionType.GET_MYTHIC_HERO.getDescription()))){
            IAPBuyManager.getInstance().triggerExsistPackageIAPModel(uid, EConditionType.GET_MYTHIC_HERO, 1, zone);
        }else if(star.contains(ColorHero.getStarFromName(EConditionType.GET_ASCENDED_HERO.getDescription()))){
            IAPBuyManager.getInstance().triggerExsistPackageIAPModel(uid, EConditionType.GET_ASCENDED_HERO, 1, zone);
        }
    }

    private void handerFinishHunt(long uid, Map<String, Object> data) {
        boolean win = (boolean) data.getOrDefault(Params.WIN, false);

        if(win){

        }else {
            UserHuntModel userHuntModel = HuntManager.getInstance().getUserHuntModel(uid, zone);
            UserCampaignDetailModel userCampaignDetailModel = CampaignManager.getInstance().getUserCampaignDetailModel(zone, uid);
            UserMissionPuzzleModel userMissionModel = MissionManager.getInstance().getUserMissionModel(uid, zone);
            UserTowerModel userTowerModel = TowerManager.getInstance().getUserTowerModel(uid, zone);

            //Dang chaper 1 bo qua xuat hien goi IAP
            String station = userCampaignDetailModel.userMainCampaignDetail.readNextStation();
            if(Integer.valueOf(station.split(",")[0]) < 1) return;

            IAPBuyManager.getInstance().triggerExsistPackageIAPModel(
                    uid,
                    EConditionType.LOSE_BATTLE,
                    userHuntModel.readLose() + userCampaignDetailModel.readLose() + userMissionModel.readLose() + userTowerModel.readLose(),
                    zone);
        }
    }

    private void handerFinishMission(long uid, Map<String, Object> data) {
        boolean win = (boolean) data.getOrDefault(Params.WIN, false);

        if(win){

        }else {
            UserHuntModel userHuntModel = HuntManager.getInstance().getUserHuntModel(uid, zone);
            UserCampaignDetailModel userCampaignDetailModel = CampaignManager.getInstance().getUserCampaignDetailModel(zone, uid);
            UserMissionPuzzleModel userMissionModel = MissionManager.getInstance().getUserMissionModel(uid, zone);
            UserTowerModel userTowerModel = TowerManager.getInstance().getUserTowerModel(uid, zone);

            //Dang chaper 1 bo qua xuat hien goi IAP
            String station = userCampaignDetailModel.userMainCampaignDetail.readNextStation();
            if(Integer.valueOf(station.split(",")[0]) < 1) return;

            IAPBuyManager.getInstance().triggerExsistPackageIAPModel(
                    uid,
                    EConditionType.LOSE_BATTLE,
                    userHuntModel.readLose() + userCampaignDetailModel.readLose() + userMissionModel.readLose() + userTowerModel.readLose(),
                    zone);
        }
    }

    private void handerFinishCampaign(long uid, Map<String, Object> data) {
        boolean win = (boolean) data.getOrDefault(Params.WIN, false);

        if(win){

        }else {
            UserHuntModel userHuntModel = HuntManager.getInstance().getUserHuntModel(uid, zone);
            UserCampaignDetailModel userCampaignDetailModel = CampaignManager.getInstance().getUserCampaignDetailModel(zone, uid);
            UserMissionPuzzleModel userMissionModel = MissionManager.getInstance().getUserMissionModel(uid, zone);
            UserTowerModel userTowerModel = TowerManager.getInstance().getUserTowerModel(uid, zone);

            //Dang chaper 1 bo qua xuat hien goi IAP
            String station = userCampaignDetailModel.userMainCampaignDetail.readNextStation();
            if(Integer.valueOf(station.split(",")[0]) < 1) return;

            IAPBuyManager.getInstance().triggerExsistPackageIAPModel(
                    uid,
                    EConditionType.LOSE_BATTLE,
                    userHuntModel.readLose() + userCampaignDetailModel.readLose() + userMissionModel.readLose() + userTowerModel.readLose(),
                    zone);
        }
    }

    private void handerFinishTower(long uid, Map<String, Object> data) {
        boolean win = (boolean) data.getOrDefault(Params.WIN, false);

        if(win){

        }else {
            UserHuntModel userHuntModel = HuntManager.getInstance().getUserHuntModel(uid, zone);
            UserCampaignDetailModel userCampaignDetailModel = CampaignManager.getInstance().getUserCampaignDetailModel(zone, uid);
            UserMissionPuzzleModel userMissionModel = MissionManager.getInstance().getUserMissionModel(uid, zone);
            UserTowerModel userTowerModel = TowerManager.getInstance().getUserTowerModel(uid, zone);

            //Dang chaper 1 bo qua xuat hien goi IAP
            String station = userCampaignDetailModel.userMainCampaignDetail.readNextStation();
            if(Integer.valueOf(station.split(",")[0]) < 1) return;

            IAPBuyManager.getInstance().triggerExsistPackageIAPModel(
                    uid,
                    EConditionType.LOSE_BATTLE,
                    userHuntModel.readLose() + userCampaignDetailModel.readLose() + userMissionModel.readLose() + userTowerModel.readLose(),
                    zone);
        }
    }
}
