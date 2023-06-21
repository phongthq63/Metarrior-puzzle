package com.bamisu.log.gameserver.module.pushnotify;

import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.gamelib.utils.PushNotifyUtils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.datamodel.pushnotify.UserPushNotifyModel;
import com.bamisu.log.gameserver.datamodel.user.UserSettingModel;
import com.bamisu.log.gameserver.module.pushnotify.entities.PushnotifyChatInfo;
import com.bamisu.log.gameserver.module.pushnotify.model.CacheTurnOnGlobalChatModel;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Create by Popeye on 3:55 PM, 12/28/2020
 */
public class PushNotifyManager {
    public Zone zone;
    public PushNotifyHandler pushNotifyHandler;
    CacheTurnOnGlobalChatModel cacheTurnOnGlobalChatModel;

    public Map<Long, PushnotifyChatInfo> unreadUserMap = new HashMap<>();

    private final ScheduledExecutorService schedulePushNotifyAfk = LizThreadManager.getInstance().getFixExecutorServiceByName("pushnotify", 1);

    public PushNotifyManager(PushNotifyHandler pushNotifyHandler) {
        this.pushNotifyHandler = pushNotifyHandler;
        this.zone = pushNotifyHandler.getParentExtension().getParentZone();
        cacheTurnOnGlobalChatModel = CacheTurnOnGlobalChatModel.copyFromDBtoObject(pushNotifyHandler.getParentExtension().getParentZone());
        initScheduler();
    }

    public void handleUpdatePushNotiID(long userID, int platform, String id) {
        UserPushNotifyModel userPushNotifyModel = UserPushNotifyModel.copyFromDBtoObject(userID, zone);
        if (userPushNotifyModel != null) {
            userPushNotifyModel.update(platform, id, zone);
        }
    }

    private void initScheduler() {
        schedulePushNotifyAfk.scheduleAtFixedRate(() -> {
            synchronized (unreadUserMap) {
                try {
                    if (!unreadUserMap.isEmpty()) {
                        List<Long> removeList = new ArrayList<>();
                        List<String> keys = new ArrayList<>();
                        for (PushnotifyChatInfo info : unreadUserMap.values()) {
                            if (ExtensionUtility.getInstance().getUserById(info.uid) == null) { //đang offline mới push về
                                keys.addAll(UserPushNotifyModel.copyFromDBtoObject(info.uid, pushNotifyHandler.getParentExtension().getParentZone()).getAllKeys());
                            }

                            //tăng số lần đã push
                            info.sendTime++;
                            if (info.sendTime >= 2) {
                                removeList.add(info.uid);
                            }
                        }

                        //push cho tất cả các device
                        if (!keys.isEmpty()) {
                            PushNotifyUtils.push(keys, "pn002");
                        }

                        //remove max time push
                        for (Long uid : removeList) {
                            if (unreadUserMap.containsKey(uid)) {
                                unreadUserMap.remove(uid);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 60 * 30, TimeUnit.SECONDS);
    }

    /**
     * khi có private chat
     *
     * @param fromUID
     * @param toUID
     */
    public void onPrivateChat(long fromUID, long toUID) {
        if (UserSettingModel.copyFromDBtoObject(toUID, pushNotifyHandler.getParentExtension().getParentZone()).pushNotificationSetting.privateMessage) {
            synchronized (unreadUserMap) {
                try {
                    if (ExtensionUtility.getInstance().getUserById(toUID) == null) {
                        if (!unreadUserMap.containsKey(toUID)) {
                            unreadUserMap.put(toUID, new PushnotifyChatInfo(toUID));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * khi có chat bang hội
     *
     * @param uids
     */
    public void onAllianceChat(Collection<Long> uids) {
        synchronized (unreadUserMap) {
            try {
                for (Long uid : uids) {
                    if (UserSettingModel.copyFromDBtoObject(uid, pushNotifyHandler.getParentExtension().getParentZone()).pushNotificationSetting.allianceChat) {
                        if (ExtensionUtility.getInstance().getUserById(uid) == null) {
                            if (!unreadUserMap.containsKey(uid)) {
                                unreadUserMap.put(uid, new PushnotifyChatInfo(uid));
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * khi có chat chanel
     *
     * @param fromUID
     * @param chanelID
     */
    public void onChanelChat(long fromUID, String chanelID) {
        synchronized (cacheTurnOnGlobalChatModel) {
            try {
                if (cacheTurnOnGlobalChatModel.mapTurnOnChanel.containsKey(chanelID)) {
                    for (Long uid : cacheTurnOnGlobalChatModel.mapTurnOnChanel.get(chanelID)) {
                        if (UserSettingModel.copyFromDBtoObject(uid, pushNotifyHandler.getParentExtension().getParentZone()).pushNotificationSetting.chanelChat) {
                            synchronized (unreadUserMap) {
                                try {
                                    if (ExtensionUtility.getInstance().getUserById(uid) == null) {
                                        if (!unreadUserMap.containsKey(uid)) {
                                            unreadUserMap.put(uid, new PushnotifyChatInfo(uid));
                                        }
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * khi có chat global
     *
     * @param fromUID
     */
    public void onGlobalChat(long fromUID) {
        synchronized (cacheTurnOnGlobalChatModel) {
            try {
                for (Long uid : cacheTurnOnGlobalChatModel.listTurnOnGlobal) {
                    if (UserSettingModel.copyFromDBtoObject(uid, pushNotifyHandler.getParentExtension().getParentZone()).pushNotificationSetting.globalChat) {
                        synchronized (unreadUserMap) {
                            try {
                                if (ExtensionUtility.getInstance().getUserById(uid) == null) {
                                    if (!unreadUserMap.containsKey(uid)) {
                                        unreadUserMap.put(uid, new PushnotifyChatInfo(uid));
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void turnOnGlobal(long uid) {
        synchronized (cacheTurnOnGlobalChatModel) {
            if (!cacheTurnOnGlobalChatModel.listTurnOnGlobal.contains(uid)) {
                cacheTurnOnGlobalChatModel.listTurnOnGlobal.add(uid);
                cacheTurnOnGlobalChatModel.saveToDB(pushNotifyHandler.getParentExtension().getParentZone());
            }
        }
    }

    public void turnOffGlobal(long uid) {
        synchronized (cacheTurnOnGlobalChatModel) {
            if (cacheTurnOnGlobalChatModel.listTurnOnGlobal.contains(uid)) {
                cacheTurnOnGlobalChatModel.listTurnOnGlobal.remove(uid);
                cacheTurnOnGlobalChatModel.saveToDB(pushNotifyHandler.getParentExtension().getParentZone());
            }
        }
    }

    public void turnOnChanel(String channelChat, long uid) {
        synchronized (cacheTurnOnGlobalChatModel) {
            if (!cacheTurnOnGlobalChatModel.mapTurnOnChanel.containsKey(channelChat)) {
                cacheTurnOnGlobalChatModel.mapTurnOnChanel.put(channelChat, new ArrayList<>());
            }

            if (!cacheTurnOnGlobalChatModel.mapTurnOnChanel.get(channelChat).contains(uid)) {
                cacheTurnOnGlobalChatModel.mapTurnOnChanel.get(channelChat).add(uid);
            }

            cacheTurnOnGlobalChatModel.saveToDB(pushNotifyHandler.getParentExtension().getParentZone());
        }
    }

    public void turnOffChanel(String channelChat, long uid) {
        synchronized (cacheTurnOnGlobalChatModel) {
            if (cacheTurnOnGlobalChatModel.mapTurnOnChanel.containsKey(channelChat)) {
                if (cacheTurnOnGlobalChatModel.mapTurnOnChanel.get(channelChat).contains(uid)) {
                    cacheTurnOnGlobalChatModel.mapTurnOnChanel.get(channelChat).remove(uid);
                    cacheTurnOnGlobalChatModel.saveToDB(pushNotifyHandler.getParentExtension().getParentZone());
                }
            }
        }
    }

    /**
     * khi bang hội có quà mưới
     *
     * @param uids
     */
    public void onAllianceHaveNewGift(Collection<Long> uids) {
        synchronized (unreadUserMap) {
            try {
                List<String> keys = new ArrayList<>();
                for (Long uid : uids) {
                    if (ExtensionUtility.getInstance().getUserById(uid) == null) {
                        keys.addAll(UserPushNotifyModel.copyFromDBtoObject(uid, pushNotifyHandler.getParentExtension().getParentZone()).getAllKeys());
                    }
                }

                //push cho tất cả các device
                if (!keys.isEmpty()) {
                    PushNotifyUtils.push(keys, "pn003");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
