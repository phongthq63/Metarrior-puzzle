package com.bamisu.log.gameserver.module.notification;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.notification.UserNotificationModel;
import com.bamisu.log.gameserver.module.notification.cmd.send.SendNotify;
import com.bamisu.log.gameserver.module.notification.cmd.send.SendNotifyModel;
import com.bamisu.log.gameserver.module.notification.config.NotificationTimeConfig;
import com.bamisu.log.gameserver.module.notification.defind.EActionNotiModel;
import com.bamisu.log.gameserver.module.notification.defind.ENotification;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class NotificationManager {

    private ScheduledExecutorService scheduledExecutor;

    private NotificationTimeConfig notificationTimeConfig;



    private static NotificationManager ourInstance = new NotificationManager();
    public static NotificationManager getInstance() {
        return ourInstance;
    }

    private NotificationManager(){
        //load config
        loadConfig();

        scheduledExecutor = LizThreadManager.getInstance().getFixExecutorServiceByName(LizThreadManager.EThreadPool.NOTIFY, 1);
    }

    private void loadConfig(){
        notificationTimeConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Notification.FILE_PATH_CONFIG_NOTIFICATION_TIME), NotificationTimeConfig.class);
    }



    /*----------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------*/
    public UserNotificationModel getNotificationModel(long uid, Zone zone){
        return UserNotificationModel.copyFromDBtoObject(uid, zone);
    }

    public List<String> getListNotifyIDLogin(long uid, Zone zone){
        return new ArrayList<>();
//        List<String> listId = new ArrayList<>();
//        for(ENotification noti : ENotification.values()){
//            listId.addAll(noti.getListNotifyID(uid, zone));
//        }
//        listId.removeAll(getListRemoveNotifyID(uid, zone));
//        return listId;
    }

    /**
     * Xoa Noti
     * @param uid
     * @param listNotify
     * @param zone
     * @return
     */
    public boolean removeNotifyID(long uid, List<String> listNotify, Zone zone){
        UserNotificationModel userNotificationModel = getNotificationModel(uid, zone);
        return removeNotifyID(userNotificationModel, listNotify, zone);
    }
    public boolean removeNotifyID(UserNotificationModel userNotificationModel, List<String> listNotify, Zone zone){
        if(userNotificationModel.removeNotify(listNotify)){
            return userNotificationModel.saveToDB(zone);
        }else {
            return true;
        }
    }

    public List<String> getListRemoveNotifyID(long uid, Zone zone){
        return getListRemoveNotifyID(getNotificationModel(uid, zone), zone);
    }
    public List<String> getListRemoveNotifyID(UserNotificationModel userNotificationModel, Zone zone){
        List<String> listId = new ArrayList<>();
        listId.addAll(userNotificationModel.readListRemoveNotify(zone));
        return listId;
    }




    /*----------------------------------------------------- SEND -----------------------------------------------*/
    public void sendNotify(long uid, List<String> listNotify, Zone zone){
        User user = ExtensionUtility.getInstance().getUserById(uid);
        sendNotify(user, listNotify, zone);
    }
    public void sendNotify(User user, List<String> listNotify, Zone zone){
        NotificationHandler handler = ((NotificationHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_NOTIFICATION));

        SendNotify objPut = new SendNotify();
        objPut.listNotify = listNotify;
        if(user != null) handler.send(objPut, user);
    }
    public void sendNotify(List<User> listUser, List<String> listNotify, Zone zone){
        NotificationHandler handler = ((NotificationHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_NOTIFICATION));

        SendNotify objPut = new SendNotify();
        objPut.listNotify = listNotify;
        if(listUser != null && !listUser.isEmpty()) handler.send(objPut, listUser.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

    /**
     * Noti show event
     * @param uid
     * @param params
     * @param zone
     */
    public void sendNotifyModel(long uid, EActionNotiModel actionNotiModel, List<String> params, Zone zone){
        sendNotifyModel(ExtensionUtility.getInstance().getUserById(uid), actionNotiModel, params, zone);
    }
    public void sendNotifyModel(List<User> users, EActionNotiModel actionNotiModel, List<String> params, Zone zone){
        NotificationHandler handler = ((NotificationHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_NOTIFICATION));

        SendNotifyModel objPut = new SendNotifyModel();
        objPut.action = actionNotiModel.getId();
        objPut.params = params;
        handler.send(objPut, users);
    }
    public void sendNotifyModel(User user, EActionNotiModel actionNotiModel, List<String> params, Zone zone){
        NotificationHandler handler = ((NotificationHandler)((ZoneExtension)zone.getExtension()).getServerHandler(Params.Module.MODULE_NOTIFICATION));

        SendNotifyModel objPut = new SendNotifyModel();
        objPut.action = actionNotiModel.getId();
        objPut.params = params;
        handler.send(objPut, user);
    }



    /*----------------------------------------------- CONFIG ---------------------------------------------------*/
    public NotificationTimeConfig getNotificationTimeConfig(){
        return notificationTimeConfig;
    }

    public Map<String, String> getNotificationTimeRefreshConfig(){
        return getNotificationTimeConfig().refresh;
    }

    public Map<String, String> getNotificationTimeRemoveConfig(){
        return getNotificationTimeConfig().remove;
    }
}
