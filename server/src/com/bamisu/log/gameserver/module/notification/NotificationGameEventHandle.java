package com.bamisu.log.gameserver.module.notification;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.datamodel.notification.UserNotificationModel;
import com.bamisu.log.gameserver.module.GameEvent.BaseGameEvent;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.bamisu.log.gameserver.module.notification.defind.ENotification;
import com.bamisu.log.gameserver.module.quest.QuestManager;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class NotificationGameEventHandle extends BaseGameEvent {

    public NotificationGameEventHandle(Zone zone) {
        super(zone);
    }

    @Override
    public void handleGameEvent(EGameEvent event, long uid, Map<String, Object> data) {
        switch (event) {
            case LEVEL_USER_UPDATE:
                handlerLeverUserUpdate(uid, data);
                break;
            case SEND_FRIEND_REQUEST:
                handlerSendFriendRequest(uid, data);
                break;
            case NEW_GIFT_GUILD:
                handlerNewGiftGuild(uid, data);
                break;
        }
    }

    @Override
    public void initEvent() {
        this.registerEvent(EGameEvent.LEVEL_USER_UPDATE);
        this.registerEvent(EGameEvent.SEND_FRIEND_REQUEST);
        this.registerEvent(EGameEvent.NEW_GIFT_GUILD);
    }


    /*-----------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------*/
    private void handlerLeverUserUpdate(long uid, Map<String, Object> data) {
        NotificationManager.getInstance().sendNotify(uid, Collections.singletonList(ENotification.HAVE_SKILL_POINT_MAGE.getNotifyID(null)), zone);
    }

    private void handlerSendFriendRequest(long uid, Map<String, Object> data) {
        List<Long> listUid = (List<Long>) data.getOrDefault(Params.UIDS, new ArrayList<>());
        List<User> listUser = new ArrayList<>();
        User user;

        try {
            for (long id : listUid) {
                user = ExtensionUtility.getInstance().getUserById(id);
                if (user != null) listUser.add(user);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
//            Logger.getLogger("catch").info(Utils.toJson(listUid));
        }
        NotificationManager.getInstance().sendNotify(listUser, Collections.singletonList(ENotification.HAVE_FRIEND_REQUEST.getNotifyID(null)), zone);
    }

    private void handlerNewGiftGuild(long uid, Map<String, Object> data) {
        List<Long> listUid = (List<Long>) data.getOrDefault(Params.UIDS, new ArrayList<>());
        List<User> listUser = listUid.stream().
                map(u -> ExtensionUtility.getInstance().getUserById(u)).
                filter(Objects::nonNull).
                collect(Collectors.toList());

        NotificationManager.getInstance().sendNotify(listUser, Collections.singletonList(ENotification.CAN_CLAIM_GIFT_GUILD.getNotifyID(null)), zone);
    }
}
