package com.bamisu.log.gameserver.module.event.event.grand_opening_checkin;

import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.log.gameserver.datamodel.event.GrandOpeningCheckInModel;
import com.bamisu.log.gameserver.module.WoL.defines.WoLConquerStatus;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.event.EventInGameHandler;
import com.bamisu.log.gameserver.module.event.cmd.send.SendClaimQuickly;
import com.bamisu.log.gameserver.module.event.cmd.send.SendCollectGiftGrandOpening;
import com.bamisu.log.gameserver.module.event.cmd.send.SendGrandOpening;
import com.bamisu.log.gameserver.module.event.event.grand_opening_checkin.entities.GrandOpeningCheckInConfig;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class GrandOpeningCheckInManager {
    private static GrandOpeningCheckInManager ourInstance = null;
    public static GrandOpeningCheckInManager getInstance(){
        if (ourInstance == null){
            ourInstance = new GrandOpeningCheckInManager();
        }
        return ourInstance;
    }

    private EventInGameHandler eventInGameHandler;
    private GrandOpeningCheckInConfig grandOpeningCheckInConfig;
    public void setEventInGameHandler(EventInGameHandler eventInGameHandler) {
        this.eventInGameHandler = eventInGameHandler;
    }

    private GrandOpeningCheckInManager(){
        grandOpeningCheckInConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Event.FILE_PATH_CONFIG_GRAND_OPENING), GrandOpeningCheckInConfig.class);
    }

    public List<ResourcePackage> getListGift(){
        return grandOpeningCheckInConfig.listGift;
    }

    public int getTime(){
        return grandOpeningCheckInConfig.time;
    }

    public ResourcePackage getGift(int position){
        return grandOpeningCheckInConfig.listGift.get(position);
    }

    public void showGrandOpening(User user, BaseExtension extension) {
        UserModel um = extension.getUserManager().getUserModel(user);
        GrandOpeningCheckInModel grandOpeningCheckInModel = getGrandOpeningCheckInModel(um.userID, user.getZone());
        if (unlockReward(grandOpeningCheckInModel)){
            grandOpeningCheckInModel.saveToDB(user.getZone());
        }
        SendGrandOpening send = new SendGrandOpening();
        send.list = grandOpeningCheckInModel.listGift;
        eventInGameHandler.send(send, user);
    }

    public void collectGiftGrandOpening(BaseExtension extension, User user, int position) {
        UserModel um = extension.getUserManager().getUserModel(user);
        GrandOpeningCheckInModel grandOpeningCheckInModel = GrandOpeningCheckInModel.copyFromDBtoObject(um.userID, user.getZone());
        if (position > grandOpeningCheckInModel.listGift.size()){
            SendCollectGiftGrandOpening send = new SendCollectGiftGrandOpening(ServerConstant.ErrorCode.ERR_SYS);
            eventInGameHandler.send(send, user);
            return;
        }
        if (grandOpeningCheckInModel.listGift.get(position) == WoLConquerStatus.INCOMPLETE.getStatus()){
            SendCollectGiftGrandOpening send = new SendCollectGiftGrandOpening(ServerConstant.ErrorCode.ERR_INCOMPLETE);
            eventInGameHandler.send(send, user);
            return;
        }else if (grandOpeningCheckInModel.listGift.get(position) == WoLConquerStatus.ALREADY_RECEIVED.getStatus()){
            SendCollectGiftGrandOpening send = new SendCollectGiftGrandOpening(ServerConstant.ErrorCode.ERR_ALREADY_RECEIVED);
            eventInGameHandler.send(send, user);
            return;
        }else{
            grandOpeningCheckInModel.listGift.set(position, WoLConquerStatus.ALREADY_RECEIVED.getStatus());
            List<ResourcePackage> list = new ArrayList<>();
            list.add(getGift(position));
            if (BagManager.getInstance().addItemToDB(list, um.userID, user.getZone(), UserUtils.TransactionType.GIFT_30_DAYS)){
                grandOpeningCheckInModel.saveToDB(user.getZone());
                SendCollectGiftGrandOpening send = new SendCollectGiftGrandOpening();
//                send.resourcePackage = getGift(position);
                eventInGameHandler.send(send, user);
            }else{
                SendCollectGiftGrandOpening send = new SendCollectGiftGrandOpening(ServerConstant.ErrorCode.ERR_SYS);
                eventInGameHandler.send(send, user);
            }
        }
    }

    public boolean unlockReward(GrandOpeningCheckInModel grandOpeningCheckInModel){
        long numberOfDays = calculateTime(grandOpeningCheckInModel.time);
        if (numberOfDays != 0){
            for (int j = 0; j < numberOfDays; j++){
                for (int count = 0; count < grandOpeningCheckInModel.listGift.size(); count++){
                    if (grandOpeningCheckInModel.listGift.get(count) == WoLConquerStatus.INCOMPLETE.getStatus()){
                        grandOpeningCheckInModel.listGift.set(count, WoLConquerStatus.CAN_RECEIVE.getStatus());
                        break;
                    }
                }
            }
            grandOpeningCheckInModel.time = getNewTime();
            return true;
        }
        return false;
    }

    private long calculateTime(long time) {
        return (getNewTime()-time)/60/60/24;
    }


    public void claimQuickly(User user, BaseExtension extension) {
        UserModel um = extension.getUserManager().getUserModel(user);
        GrandOpeningCheckInModel player = GrandOpeningCheckInModel.copyFromDBtoObject(um.userID, user.getZone());
        List<ResourcePackage> list = new ArrayList<>();
        for (int i = 0; i< player.listGift.size(); i++){
            if (player.listGift.get(i) == WoLConquerStatus.CAN_RECEIVE.getStatus()){
                list.add(getGift(i));
                player.listGift.set(i, WoLConquerStatus.ALREADY_RECEIVED.getStatus());
            }
        }
        if (list.size() == 0){
            SendClaimQuickly send = new SendClaimQuickly(ServerConstant.ErrorCode.ERR_ALREADY_RECEIVED);
            eventInGameHandler.send(send, user);
            return;
        }
        player.saveToDB(user.getZone());
        if (BagManager.getInstance().addItemToDB(list, um.userID, user.getZone(), UserUtils.TransactionType.GIFT_30_DAYS)){
            SendClaimQuickly send = new SendClaimQuickly();
            eventInGameHandler.send(send, user);
        }else{
            SendClaimQuickly send = new SendClaimQuickly(ServerConstant.ErrorCode.ERR_SYS);
            eventInGameHandler.send(send, user);
        }
    }

    public GrandOpeningCheckInModel getGrandOpeningCheckInModel(long uid, Zone zone){
        GrandOpeningCheckInModel grandOpeningCheckInModel = GrandOpeningCheckInModel.copyFromDBtoObject(uid, zone);
        if(unlockReward(grandOpeningCheckInModel)) grandOpeningCheckInModel.saveToDB(zone);
        return grandOpeningCheckInModel;
    }

    public long getNewTime(){ZoneId zoneId = ZoneId.systemDefault() ;
        LocalDate today = LocalDate.now( zoneId  ) ;

        ZonedDateTime zdtStart = today.atStartOfDay( zoneId ) ;

        Timestamp start = Timestamp.valueOf(zdtStart.toLocalDate().atStartOfDay());
        return start.getTime()/1000;

    }
}
