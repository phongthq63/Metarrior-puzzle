package com.bamisu.log.gameserver.datamodel.tower;

import com.bamisu.log.gameserver.module.tower.TowerManager;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

public class CacheRankTowerModel extends DataModel {
    private static final long id = 0;

    public String jsonData;
    public int timeStamp;
    private final static int timeRefresh = TowerManager.getInstance().getRankTowerConfig().timeCache;

    public static CacheRankTowerModel createCache(Zone zone){
        CacheRankTowerModel cacheRankTowerModel = new CacheRankTowerModel();
        cacheRankTowerModel.timeStamp = Utils.getTimestampInSecond();

        return cacheRankTowerModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.id), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static CacheRankTowerModel copyFromDBtoObject(Zone zone) {
        CacheRankTowerModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(id), CacheRankTowerModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, CacheRankTowerModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        if(pInfo == null){
            pInfo = CacheRankTowerModel.createCache(zone);
        }
        return pInfo;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * doc cache
     * @param zone
     * @return
     */
    public String readCache(Zone zone){
        if(isTimeRefresh()){
            jsonData = "";
            timeStamp = Utils.getTimestampInSecond();
            saveToDB(zone);
        }
        return jsonData;
    }

    /**
     * kiem tra co cache khong
     * @return
     */
    public boolean haveCache(){
        if(isTimeRefresh())return false;
        return jsonData != null || !jsonData.isEmpty();
    }

    public void updateCache(String cache, Zone zone){
        jsonData = cache;
        saveToDB(zone);
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    private boolean isTimeRefresh(){
        return Utils.getTimestampInSecond() - timeStamp > timeRefresh;
    }
}
