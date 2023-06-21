package com.bamisu.log.gameserver.datamodel.tower;

import com.bamisu.log.gameserver.datamodel.tower.entities.UserTowerInfo;
import com.bamisu.log.gameserver.module.tower.TowerManager;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TowerManagerModel extends DataModel {
    private static final long id = 0;

    public int floor;
    public int timeStamp;
    private static final short maxSave = TowerManager.getInstance().getRankTowerConfig().save;
    public List<UserTowerInfo> rank = new ArrayList<>();        //Da dc sap xep


    private final static Object lockRank = new Object();



    public static TowerManagerModel createTowerManagerModel(Zone zone){
        TowerManagerModel towerManagerModel = new TowerManagerModel();
        towerManagerModel.floor = 0;
        towerManagerModel.timeStamp = 0;
        towerManagerModel.saveToDB(zone);

        return towerManagerModel;
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


    public static TowerManagerModel copyFromDBtoObject(Zone zone) {
        TowerManagerModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(id), TowerManagerModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, TowerManagerModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        if(pInfo == null){
            pInfo = TowerManagerModel.createTowerManagerModel(zone);
        }
        return pInfo;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Kiem tra xem user do co trong rank khong
     * @param uid
     * @return
     */
    private boolean inRank(long uid){
        return rank.parallelStream().map(obj -> obj.uid).collect(Collectors.toSet()).contains(uid);
    }

    private void updateSaveCache(UserTowerInfo userTowerInfo){
        floor = userTowerInfo.floor;
        timeStamp = userTowerInfo.timeStamp;
    }

    public boolean updateRank(UserTowerInfo userTowerInfo){
        UserTowerInfo userInTower;

        synchronized (lockRank){
            //TH trong top rank
            if(inRank(userTowerInfo.uid)){
                boolean flag = false;
                for(int i = rank.size() - 1; i >= 0; i--){
                    userInTower = rank.get(i);
                    //Flag == true -> uid bi xoa -> rank.get(i).uid != userTowerInfo.uid
                    //tim kiem rank cua nguoi choi
                    if(userInTower.uid == userTowerInfo.uid){
                        //TH nguoi choi van rank cu
                        if(userInTower.floor >= userTowerInfo.floor){
                            return false;
                        }
                        //TH nguoi choi len rank moi
                        //Xoa nguoi choi tai rank hien tai
                        rank.remove(i);
                        flag = true;
                        continue;
                    }
                    //Chen nguoi choi vao rank moi (day tat ca thang xuong 1 rank)
                    if(flag){
                        //Tim kiem nguoi choi floor cao hon minh
                        if(userInTower.floor >= userTowerInfo.floor){
                            rank.add(i + 1, userTowerInfo);
                            updateSaveCache(rank.get(rank.size() - 1));
                            return true;
                        }
                    }
                }
                //TH minh rank cao nhat
                rank.add(0, userTowerInfo);
                updateSaveCache(rank.get(rank.size() - 1));
                return true;

            }else {
                //TH ngoai top rank
                //TH rank chua du 50 nguoi
                if(rank.size() < maxSave){
                    rank.add(userTowerInfo);
                    updateSaveCache(userTowerInfo);
                    return true;

                }else {
                    //Tang thap nhat trong rank (thay the thang thap nhat trong rank) --- Len tung tang 1
                    if(userTowerInfo.floor > floor){
                        //Chen nguoi choi vao rank moi (day tat ca thang xuong 1 rank)
                        for(int i = rank.size() - 1; i >= 0; i--){
                            userInTower = rank.get(i);
                            //Tim kiem nguoi choi floor cao hon minh
                            if(userInTower.floor >= userTowerInfo.floor){
                                rank.add(i + 1, userTowerInfo);
//                                rank.remove(rank.size() - 1);
                                rank = rank.parallelStream().limit(maxSave).collect(Collectors.toList());
                                updateSaveCache(rank.get(rank.size() - 1));
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Xu ly ngoai le rank
     * Neu co 1 nguoi o 2 rank
     */
    public void executionRank(){
        synchronized (lockRank){
            //Xu ly xoa cac index giong nhau neu co
            List<Short> indexDulicate = new ArrayList<>();
            Map<Long, Short> mapRank = new HashMap<>();
            UserTowerInfo userTowerInfo;

            for(short i = 0; i < rank.size() - 1; i++){
                userTowerInfo = rank.get(i);
                if(mapRank.containsKey(userTowerInfo.uid)){
                    indexDulicate.add(i);
                }else {
                    mapRank.put(userTowerInfo.uid, i);
                }
            }

            if(!indexDulicate.isEmpty()){
                short index;
                for(int i = indexDulicate.size() - 1; i >= 0; i--){
                    index = indexDulicate.get(i);
                    rank.remove(index);
                }
            }

            //Sort lai tower --> do bug nen viet tam
            rank = rank.parallelStream().sorted((i1, i2) -> {
                if(i1.floor != i2.floor){
                    return i2.floor - i1.floor;
                }else {
                    return i1.timeStamp - i2.timeStamp;
                }
            }).collect(Collectors.toList());
        }
    }
}
