package com.bamisu.log.gameserver.datamodel.notification;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.IAP.TimeUtils;
import com.bamisu.log.gameserver.module.IAP.defind.ETimeType;
import com.bamisu.log.gameserver.module.notification.NotificationManager;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;

public class UserNotificationModel extends DataModel {

    public long uid;
    public Map<String,Integer> mapRemoveNoti = new HashMap<>();


    private final Object lockNotify = new Object();



    private void initNotification(Zone zone){

    }
    private void init(Zone zone){
        initNotification(zone);
    }

    public static UserNotificationModel createUserNotificationModel(long uid, Zone zone){
        UserNotificationModel notificationModel = new UserNotificationModel();
        notificationModel.uid = uid;
        notificationModel.init(zone);
        notificationModel.saveToDB(zone);

        return notificationModel;
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

    public static UserNotificationModel copyFromDBtoObject(long uId, Zone zone) {
        UserNotificationModel notificationModel = copyFromDBtoObject(String.valueOf(uId), zone);
        if (notificationModel == null) {
            notificationModel = createUserNotificationModel(uId, zone);
        }
        return notificationModel;
    }

    private static UserNotificationModel copyFromDBtoObject(String uId, Zone zone) {
        UserNotificationModel pInfo = null;
        try {
            String str = (String) getModel(uId, UserNotificationModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserNotificationModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }



    /*----------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------*/
    public Set<String> readListRemoveNotify(Zone zone){
        Map<String,String> cfTimeRefresh = NotificationManager.getInstance().getNotificationTimeRefreshConfig();
        boolean haveSave = false;

        synchronized (lockNotify){
            Iterator<Map.Entry<String, Integer>> iterator = mapRemoveNoti.entrySet().iterator();
            Map.Entry<String, Integer> entry;
            while (iterator.hasNext()){
                entry = iterator.next();

                if(cfTimeRefresh.containsKey(entry.getKey()) &&
                        TimeUtils.isTimeTo(ETimeType.fromID(cfTimeRefresh.get(entry.getKey())), entry.getValue())){
                    iterator.remove();
                    haveSave = true;
                }
            }

            if(haveSave) saveToDB(zone);
            return mapRemoveNoti.keySet();
        }
    }

    public boolean removeNotify(List<String> listNotiId){
        boolean haveRemove = false;
        int now = Utils.getTimestampInSecond();

        synchronized (lockNotify){
            for(String id : listNotiId){
                mapRemoveNoti.put(id, now);
                haveRemove = true;
            }
            return haveRemove;
        }
    }
}
