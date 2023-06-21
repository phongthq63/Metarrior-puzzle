package com.bamisu.log.gameserver.datamodel.WoL;

import com.bamisu.log.gameserver.module.WoL.WoLManager;
import com.bamisu.log.gameserver.module.WoL.entities.*;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WoLModel extends DataModel {
    public static long uid = 0;
    public List<WoLPlayerAchievementVO> listPlayer = new ArrayList<>(); //ID area - list id player
    public Map<Integer, List<WoLConquerVO>> mapConquer = new HashMap<>(); //ID area - list status conquer
    public WoLModel(){
        init();
    }

    private void init() {
        List<WoLPlayerAchievementVO> listPlayer = new ArrayList<>();
        for (int area = 0; area< WoLManager.getInstance().getWoLConfig().size(); area++){ //area

            //List Player
            WoLPlayerAchievementVO woLPlayerAchievementVO = new WoLPlayerAchievementVO();
            woLPlayerAchievementVO.area = area;
            List<WoLPlayerInStageVO> listPlayerStage = new ArrayList<>();

            //Map Conquer
            List<WoLConquerVO> listConquer = new ArrayList<>();
            for (int stage = 0; stage < WoLManager.getInstance().getWoLConfig().get(area).list.size(); stage++){ //stage

                //This Player
                WoLPlayerInStageVO woLPlayerInStageVO = new WoLPlayerInStageVO();
                woLPlayerInStageVO.stage = stage;
                woLPlayerInStageVO.uid = -1;
                //TEST
//                long userID = 10058;
//                woLPlayerInStageVO.uid = userID;
//                userID+=2;
                //-----
                listPlayerStage.add(woLPlayerInStageVO);

                //Map Conquer
                WoLConquerVO woLConquerVO = new WoLConquerVO();
                woLConquerVO.stage = stage;


                List<GeneralConquerVO> listGeneralConquer = new ArrayList<>();
                for (int challenge = 0; challenge < WoLManager.getInstance().getStage(stage, area).listChallenges.size(); challenge++){
                    GeneralConquerVO generalConquerVO = new GeneralConquerVO(false, -1); //True: achieved - False: don't have
                    listGeneralConquer.add(generalConquerVO);

                    //TEST
//                    GeneralConquerVO generalConquerVO = new GeneralConquerVO(true, userID); //True: achieved - False: don't have
//                    listGeneralConquer.add(generalConquerVO);
                    //--------
                }
                woLConquerVO.listConquer = listGeneralConquer;
                listConquer.add(woLConquerVO);
            }
            woLPlayerAchievementVO.listPlayer = listPlayerStage;
            listPlayer.add(woLPlayerAchievementVO);
            this.mapConquer.put(area, listConquer);
        }
        this.listPlayer = listPlayer;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(uid), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static WoLModel copyFromDBtoObject(Zone zone) {
        WoLModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(uid), WoLModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, WoLModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {
        }
        if (pInfo == null) {
            pInfo = new WoLModel();
            pInfo.saveToDB(zone);
        }
        return pInfo;
    }
}
