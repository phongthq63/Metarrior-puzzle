package com.bamisu.log.gameserver.datamodel.mission;

import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.mission.entities.MissionInfo;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.mission.MissionManager;
import com.bamisu.log.gameserver.module.mission.config.entities.MissionVO;
import com.bamisu.log.gameserver.module.mission.defind.EMissionAction;
import com.bamisu.log.gameserver.module.mission.defind.EMissionStatus;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.log.gameserver.module.vip.defines.EGiftVip;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;

public class UserMissionPuzzleModel extends DataModel {

    public long uid;
    public List<MissionInfo> mission = new ArrayList<>();
    //Save mission ngay hom truoc de tranh TH nguoi choi lam nhie vu ngay hom truoc hoan thanh vao ngay hom sau
    public List<MissionInfo> missionOld = new ArrayList<>();
    public int timeStampAdd;
    public int timeStamp;

    public long winMission;
    public int lose;



    private void init(Zone zone){
        initMission(zone);
    }

    /**
     * Gen mission
     */
    private void initMission(Zone zone){
        //Theo horor user + predium
        initMission(VipManager.getInstance().getBonus(uid, zone, EGiftVip.EXTRA_MISSION), zone);
    }
    public void initMission(int countMission, Zone zone){
        int levelSage = BagManager.getInstance().getLevelUser(uid, zone);

        for(int i = 0; i < countMission; i++){
            mission.add(MissionInfo.create(levelSage));
        }
    }

    public static UserMissionPuzzleModel createUserMission(long uid, Zone zone) {
        UserMissionPuzzleModel userMissionModel = new UserMissionPuzzleModel();
        userMissionModel.uid = uid;
        userMissionModel.init(zone);
        userMissionModel.timeStamp = Utils.getTimestampInSecond();
        userMissionModel.timeStampAdd = userMissionModel.timeStamp;
        userMissionModel.saveToDB(zone);

        return userMissionModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uid), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserMissionPuzzleModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    private static UserMissionPuzzleModel copyFromDBtoObject(String uId, Zone zone) {
        UserMissionPuzzleModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserMissionPuzzleModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserMissionPuzzleModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }



    /*---------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Lam moi hoan toan ngay moi
     */
    private void refreshDay(Zone zone){
        timeStamp = Utils.getTimestampInSecond();
        //Luu nv ngay hom trc vao list khac
        missionOld.clear();
        missionOld.addAll(mission);
        //Xoa toan bo nhiem vu cua ngay hom truoc (ke ca da nhan) + tao moi nhiem vu
        mission.clear();
        initMission(zone);
        //Luu data
        saveToDB(zone);
    }

    /**
     * Tang them mission
     * @param zone
     */
    public void addExtraMission(Zone zone){
        //Cho them 1 mission
        initMission(MissionManager.getInstance().getMissionAddConfig().count, zone);
        //Luu data
        saveToDB(zone);
    }

    /**
     * Lay nhiem vu trong ngay
     * @param zone
     * @return
     */
    public List<MissionInfo> readMission(Zone zone){
        if(isNewDay()){
            refreshDay(zone);
        }
        if(mission.stream().filter(obj -> obj.status.equals(EMissionStatus.DOING.getId())).count() <= 0 &&
                isTimeAddMission()){
            addExtraMission(zone);
        }

        return mission;
    }
    public List<MissionInfo> readMissionOld(){
        return missionOld;
    }

    /**
     * Xu ly nhiem vu
     */
    public boolean executionMission(String hash, EMissionAction action, Zone zone){
        //Lay nhiem vu ra
        out_loop:
        for(MissionInfo mission : mission){
            if(mission.hash.equals(hash)){
                switch (action){
                    case DO:
                        if(!mission.status.equals(EMissionStatus.DOING.getId())){
                            break out_loop;
                        }
                        mission.redo++;
                        if(mission.redo >= MissionManager.getInstance().getCountRedoMission()) mission.status = EMissionStatus.COMPLETED.getId();

                        return saveToDB(zone);

                    default:
                        return false;
                }
            }
        }
        //Khong tim thay nhiem vu
        return false;
    }

    public boolean updateStatusMision(String hash, EMissionStatus status, boolean isWin, Zone zone){
        //Nhiem vu hien tai
        for(MissionInfo mission : mission){
            if(mission.hash.equals(hash)){
                mission.updateStatusMission(status, isWin);
                //Cap nhat time them mission
                timeStampAdd = Utils.getTimestampInSecond();
                return saveToDB(zone);
            }
        }
        //Neu nhiem vu cu (nhan ngay hom trc - hoan thanh ngay hom sau)
        for(MissionInfo mission : missionOld){
            if(mission.hash.equals(hash)){
                mission.updateStatusMission(status, isWin);
                //Cap nhat time them mission
                timeStampAdd = Utils.getTimestampInSecond();
                return saveToDB(zone);
            }
        }

        return false;
    }



    /*--------------------------------------------------------------------------------------------------------------*/
    public int readLose(){
        return lose;
    }

    public boolean increaseLose(Zone zone){
        lose++;
        return saveToDB(zone);
    }

    /**
     * Kiem tra ngay moi chua
     */
    private boolean isNewDay(){
        return Utils.isNewDay(timeStamp);
    }
    private boolean isTimeAddMission(){
        return Utils.getTimestampInSecond() - timeStampAdd >= MissionManager.getInstance().getMissionAddConfig().refresh;
    }
}
