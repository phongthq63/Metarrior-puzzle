package com.bamisu.log.gameserver.datamodel.store;

import com.bamisu.log.gameserver.module.store.StoreManager;
import com.bamisu.log.gameserver.module.store.entities.StoreDataVO;
import com.bamisu.log.gameserver.module.store.entities.StoreVO;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class StoreModel extends DataModel {
    public long uid;
    public List<StoreDataVO> listStore = new ArrayList<>();

    public StoreModel() {
    }

    public StoreModel(long uid){
        this.uid = uid;
        init();
    }

    private void init() {
        List<StoreVO> listStore = StoreManager.getInstance().getListStoreConfig();
        List<StoreDataVO> listData = new ArrayList<>();
        for (StoreVO storeVO: listStore){
            StoreDataVO storeDataVO = new StoreDataVO();
            storeDataVO.idStore = storeVO.id;
            storeDataVO.count = storeVO.refresh.size();
            storeDataVO.listItem = StoreManager.getInstance().getListItemStore(storeDataVO.idStore);
//            storeDataVO.time = Utils.getTimestampInSecond() + (StoreManager.getInstance().getStoreDependOnId(storeDataVO.idStore).time * 24 * 60 *60);
            storeDataVO.time = StoreManager.getInstance().getTimeRefresh(StoreManager.getInstance().getStoreDependOnId(storeDataVO.idStore).time);
//            storeDataVO.time = Utils.getTimestampInSecond() + 180;
            listData.add(storeDataVO);
        }
        this.listStore = listData;
    }

//    private long getTimeRefresh(int days){
//        ZoneId zoneId = ZoneId.systemDefault();
//        LocalDate today = LocalDate.now();
//        ZonedDateTime newDay = today.plusDays(days).atStartOfDay(zoneId);
//        System.out.println(newDay.toLocalDate().atStartOfDay());
//
//        Timestamp timestamp = Timestamp.valueOf(newDay.toLocalDate().atStartOfDay());
//        return timestamp.getTime()/1000;
//    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uid), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static StoreModel copyFromDBtoObject(long uid, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uid), zone);
    }

    public static StoreModel copyFromDBtoObject(String uid, Zone zone) {
        StoreModel pInfo = null;
        try {
            String str = (String) getModel(uid, StoreModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, StoreModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {

        }
        if (pInfo == null) {
            pInfo = new StoreModel(Long.parseLong(uid));
            pInfo.saveToDB(zone);
        }
        return pInfo;
    }

    public static StoreModel create(long uid, Zone zone) {
        StoreModel d = new StoreModel(uid);
        if (d.saveToDB(zone)) {
            return d;
        }
        return null;
    }
}
