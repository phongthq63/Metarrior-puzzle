package com.bamisu.log.gameserver.entities;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.manager.ServerManager;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyManager;
import com.bamisu.log.gameserver.module.adventure.AdventureManager;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.event.EventInGameManager;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.notification.NotificationManager;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateAccount;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateVip;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.EVip;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import org.apache.thrift.TException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Popeye on 7/12/2017.
 */
public class CMDUtils {
    public static void putLoginData(byte action, ISFSObject data, UserModel userModel, int serverID, Zone zone) {
        data.putByte(Params.IS_NULL, (byte) 0);
        data.putByte(Params.ACTION, action);
        data.putInt(Params.SERVER_ID, serverID);
        data.putLong(Params.USER_ID, userModel.userID);
        data.putUtfString(Params.ACCOUNT_ID, userModel.accountID);
        data.putUtfString(Params.USER_DISPLAY_NAME, userModel.displayName);
        data.putInt(Params.LEVEL, BagManager.getInstance().getLevelUser(userModel.userID, zone));
        data.putInt(Params.HONOR, 1);
        data.putInt(Params.CP, 1);
//            data.putInt(Params.HONOR, VipManager.getInstance().getVipHonor(userModel.userID, zone));
//            data.putInt(Params.CP, HeroManager.getInstance().getPower(userModel.userID, zone));
        boolean isLinkedUsername = false;
        try {
            Map<String, String> linkedAccount = SDKGateAccount.getLinkedAccount(userModel.accountID);
            isLinkedUsername = linkedAccount.containsKey(Params.USER_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        data.putBool(Params.IS_LINKED_USERNAME, isLinkedUsername);
        data.putUtfString(Params.AVATAR_ID, userModel.avatar);
        data.putInt(Params.AVATAR_FRAME, userModel.avatarFrame);
        data.putUtfString(Params.TOKEN, userModel.access_token);
        data.putUtfString(Params.LANGUAGE, userModel.lang);
//            data.putIntArray(Params.LINKED, userModel.linked);
        data.putIntArray(Params.LINKED, new ArrayList<>());
        data.putUtfString(Params.TIME_ZONE, ServerConstant.TIME_ZONE);
        data.putInt(Params.TIMESTAMP_SERVER, Utils.getTimestampInSecond());
        data.putUtfString(Params.TIME_SERVER, LocalDateTime.now(ServerConstant.TIME_ZONE_ID).format(DateTimeFormatter.ofPattern(ServerConstant.DATE_TIME_FORMAT)));
        data.putInt(Params.AFK_TIME, AdventureManager.getInstance().getAFKTime(userModel.userID, zone));
        data.putInt(Params.ARCHMAGE, 1);
//        data.putInt(Params.ARCHMAGE, VipManager.getInstance().getVip(EVip.ARCHMAGE, userModel.accountID).expired);
        data.putInt(Params.PROTECTOR, 1);
//        data.putInt(Params.PROTECTOR, VipManager.getInstance().getVip(EVip.PROTECTOR, userModel.accountID).expired);
        data.putUtfString(Params.STAGE, userModel.stage);
        data.putIntArray(Params.STAGE_V2, userModel.stageV3);
        data.putBool(Params.EVENT, false);
        data.putBool(Params.IAP, false);
        data.putBool(Params.CHECKIN, false);
//            data.putBool(Params.EVENT, !EventInGameManager.getInstance().getListCurrentEvent(userModel.userID, zone).isEmpty());
//            data.putBool(Params.IAP, !IAPBuyManager.getInstance().getListTabSpecial(userModel.userID, zone).isEmpty() ||
//                    !EventInGameManager.getInstance().getEventSpecial(zone).isEmpty());
//            data.putBool(Params.CHECKIN, IAPBuyManager.getInstance().getInfoIAPChallengeUserModel(userModel.userID, "welcome", zone) != null);
        data.putUtfStringArray(Params.NOTIFY, NotificationManager.getInstance().getListNotifyIDLogin(userModel.userID, zone));
        data.putBool(Params.IS_ACTIVE, true);
        data.putBool(Params.FIRST_SERVER, true);

//            data.putBool(Params.IS_ACTIVE, ServerManager.getInstance().isActiveEventModule(zone));

//            System.out.println();
//            Logger.getLogger("catch").info(Utils.toJson(NotificationManager.getInstance().getListNotifyIDLogin(userModel.userID, zone)));
//            System.out.println();
//
//            try {
//                data.putBool(Params.FIRST_SERVER, SDKGateVip.canTakeFeeVip(userModel.accountID));
//            } catch (TException e) {
//                data.putBool(Params.FIRST_SERVER, false);
//                e.printStackTrace();
//            }
    }
}
