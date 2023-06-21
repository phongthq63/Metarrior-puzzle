package com.bamisu.log.gameserver.module.IAPBuy;

import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.sql.game.dbo.IAPPackageDBO;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.IAP.event.entities.InfoIAPSale;
import com.bamisu.log.gameserver.datamodel.IAP.home.UserIAPHomeModel;
import com.bamisu.log.gameserver.datamodel.IAP.store.UserIAPStoreModel;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoIAPChallenge;
import com.bamisu.log.gameserver.datamodel.IAP.entities.InfoIAPPackage;
import com.bamisu.log.gameserver.datamodel.guild.entities.GiftGuildDescription;
import com.bamisu.log.gameserver.datamodel.guild.entities.GiftGuildInfo;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.IAP.IAPManager;
import com.bamisu.log.gameserver.module.IAP.defind.EFlatform;
import com.bamisu.log.gameserver.module.IAPBuy.cmd.rec.RecClaimIAPPackage;
import com.bamisu.log.gameserver.module.IAPBuy.cmd.rec.RecClaimIAPChallenge;
import com.bamisu.log.gameserver.module.IAPBuy.cmd.rec.RecGetInfoIAPTab;
import com.bamisu.log.gameserver.module.IAPBuy.cmd.send.*;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EIAPClaimType;
import com.bamisu.log.gameserver.module.IAPBuy.defind.EIAPType;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPChallengeVO;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPPackageVO;
import com.bamisu.log.gameserver.module.IAPBuy.config.entities.IAPTabVO;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.log.gameserver.module.event.EventInGameManager;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.log.gameserver.module.guild.define.EGuildGiftType;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateAccount;
import com.bamisu.log.gameserver.sql.store.dao.IAPPackageDAO;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IAPBuyHandler extends ExtensionBaseClientRequestHandler {

    private Logger logger = Logger.getLogger("iapDebug");

    public UserModel getUserModel(long uid) {
        return extension.getUserManager().getUserModel(uid);
    }

    public IAPBuyHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_IAP_STORE;
        new IAPGameEventHandle(extension.getParentZone());
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_GET_INFO_IAP_TAB:
                doGetInfoIAPTab(user, data);
                break;
            case CMD.CMD_CLAIM_IAP_PACKAGE_ITEM:
                doClaimIAPPackage(user, data);
                break;
            case CMD.CMD_CLAIM_IAP_REWARD_CHALLENGE:
                doClaimIAPChallenge(user, data);
                break;
            case CMD.CMD_GET_LIST_IAP_TAB_SPECIAL:
                doGetListIAPTabSpecial(user, data);
                break;
        }
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_IAP_STORE, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_IAP_STORE, this);
    }



    /*-----------------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------------*/
    /**
     * Thong tin tab trong iap store
     * @param user
     * @param data
     */
    @WithSpan
    private void doGetInfoIAPTab(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecGetInfoIAPTab objGet = new RecGetInfoIAPTab(data);

        IAPTabVO tabCf = IAPBuyManager.getInstance().getIAPTabConfig(objGet.id);
        if(tabCf == null){
            SendGetInfoIAPTab objPut = new SendGetInfoIAPTab(ServerConstant.ErrorCode.ERR_NOT_EXSIST_TAB);
            send(objPut, user);
            return;
        }

        UserIAPStoreModel userIAPStoreModel = IAPBuyManager.getInstance().getUserIAPStoreModel(uid, getParentExtension().getParentZone());
        UserIAPHomeModel userIAPHomeModel = IAPBuyManager.getInstance().getUserIAPHomeModel(uid, getParentExtension().getParentZone());

        EIAPType typeTab = EIAPType.fromID(tabCf.type);
        List<InfoIAPPackage> packData = null;
        InfoIAPChallenge challengeData = null;
        switch (typeTab){
            case PACKAGE_ITEM:
                packData = IAPBuyManager.getInstance().getInfoIAPPackageUserModelDependByTab(uid, tabCf.id, getParentExtension().getParentZone());
                break;
            case CHALLENGE:
                challengeData = IAPBuyManager.getInstance().getInfoIAPChallengeUserModel(uid, tabCf.packages.get(0), getParentExtension().getParentZone());
                break;
        }


        SendGetInfoIAPTab objPut = new SendGetInfoIAPTab();
        objPut.tabCf = tabCf;
        objPut.type = typeTab;
        objPut.userIAPStoreModel = userIAPStoreModel;
        objPut.userIAPHomeModel = userIAPHomeModel;
        objPut.listSale = IAPBuyManager.getInstance().getInfoIAPSale(getParentExtension().getParentZone());
        objPut.listData = packData;
        objPut.infoData = challengeData;
        objPut.zone = getParentExtension().getParentZone();
        send(objPut, user);
    }



    /**
     * Lay item goi trong iap store
     * @param user
     * @param data
     */
    @WithSpan
    private void doClaimIAPPackage(User user, ISFSObject data) {
        UserModel um = extension.getUserManager().getUserModel(user);
        long uid = um.userID;
        Zone zone = getParentExtension().getParentZone();

        RecClaimIAPPackage objGet = new RecClaimIAPPackage(data);
        logger.info("s" + ((ZoneExtension) extension).getServerID() + "|send|" + objGet.data.toJson());
        UserIAPStoreModel userIAPStoreModel = IAPBuyManager.getInstance().getUserIAPStoreModel(uid, zone);
        UserIAPHomeModel userIAPHomeModel = IAPBuyManager.getInstance().getUserIAPHomeModel(uid, zone);
        String idData = objGet.productId;
        String idAPI = objGet.productId;

        //Chuyen doi idSale ---> id (Neu dang sale)
        InfoIAPSale dataIAPSale = IAPBuyManager.getInstance().getInfoIAPSale(uid, objGet.productId, zone);
        if(dataIAPSale != null) idData = dataIAPSale.id;

        //Kiem tra package co ton tai khong
        IAPPackageVO packageCf = IAPBuyManager.getInstance().getIAPPackageConfig(idData, zone);
        if(packageCf == null){
            SendClaimIAPPackage objPut = new SendClaimIAPPackage(ServerConstant.ErrorCode.ERR_NOT_EXSIST_PACKAGE);
            send(objPut, user);
            return;
        }

        //Kiem tra co the mua ko
        if(!IAPBuyManager.getInstance().canClaimIAPPackage(userIAPStoreModel, userIAPHomeModel, idData, zone)){
            SendClaimIAPPackage objPut = new SendClaimIAPPackage(ServerConstant.ErrorCode.ERR_CAN_NOT_CLAIM_PACKAGE);
            send(objPut, user);
            return;
        }

        if(!extension.isTestServer() && IAPBuyManager.getInstance().haveCheckPaymentIAP(idData, zone)){

            if(!IAPManager.getInstance().checkPaymentIAP(uid, EFlatform.fromStrValue(objGet.flatform), objGet.packageName, idAPI, objGet.purchaseToken, getParentExtension().getParentZone())){
                SendClaimIAPPackage objPut = new SendClaimIAPPackage(ServerConstant.ErrorCode.ERR_NOT_HAVE_PAYMENT);
                send(objPut, user);
                return;
            }

            logger.info("s" + ((ZoneExtension) extension).getServerID() + "|" + "success|" + uid + "|" + objGet.flatform + "|" + um.accountID + "|" + objGet.productId + "|" + objGet.purchaseToken + "|" + objGet.transactionId);

            switch (EFlatform.fromStrValue(objGet.flatform)){
                case ANDROID:
                case MENA:
                    //Luu vao sql
                    IAPPackageDAO.save(IAPPackageDBO.create(uid, idAPI, objGet.purchaseToken, objGet.transactionId, EFlatform.fromStrValue(objGet.flatform).getIntValue()), zone);

                    //send to sdk
                    SDKGateAccount.logIAP(um.accountID, um.serverId, um.userID, EFlatform.fromStrValue(objGet.flatform).getIntValue(), idAPI, objGet.purchaseToken, objGet.transactionId);

                    //Nhan phan thuong - create vao database
                    if(!IAPBuyManager.getInstance().rewardIAPPackage(userIAPStoreModel, userIAPHomeModel, idData, objGet.purchaseToken, zone)){
                        SendClaimIAPPackage objPut = new SendClaimIAPPackage(ServerConstant.ErrorCode.ERR_SYS);
                        send(objPut, user);
                        return;
                    }
                    break;
                case IOS:
                    //Luu vao sql
                    IAPPackageDAO.save(IAPPackageDBO.create(uid, idAPI, objGet.transactionId, objGet.transactionId, EFlatform.fromStrValue(objGet.flatform).getIntValue()), zone);

                    //send to sdk
                    SDKGateAccount.logIAP(um.accountID, um.serverId, um.userID, EFlatform.fromStrValue(objGet.flatform).getIntValue(), idAPI, objGet.transactionId, objGet.transactionId);

                    //Nhan phan thuong - create vao database
                    if(!IAPBuyManager.getInstance().rewardIAPPackage(userIAPStoreModel, userIAPHomeModel, idData, objGet.transactionId, zone)){
                        SendClaimIAPPackage objPut = new SendClaimIAPPackage(ServerConstant.ErrorCode.ERR_SYS);
                        send(objPut, user);
                        return;
                    }
                    break;
            }
        }else {
            //Nhan phan thuong - create vao database
            if(!IAPBuyManager.getInstance().rewardIAPPackage(userIAPStoreModel, userIAPHomeModel, idData, null, zone)){
                SendClaimIAPPackage objPut = new SendClaimIAPPackage(ServerConstant.ErrorCode.ERR_SYS);
                send(objPut, user);
                return;
            }
        }

        //Them do do vao tui
        List<ResourcePackage> listResource = packageCf.reward;
        List<ResourcePackage> listGiftResource = packageCf.reward.stream().
                filter(obj -> obj.id.equals("SPI1128") || obj.id.equals("SPI1129") || obj.id.equals("SPI1130") || obj.id.equals("SPI1131") || obj.id.equals("SPI1132") || obj.id.equals("SPI1133") || obj.id.equals("SPI1134") || obj.id.equals("SPI1135") || obj.id.equals("SPI1136") || obj.id.equals("SPI1137")).
                collect(Collectors.toList());
        int now = Utils.getTimestampInSecond();
        if(!BagManager.getInstance().addItemToDB(listResource, uid, zone, UserUtils.TransactionType.DO_IAP_PACKAGE) ||
                !GuildManager.getInstance().addGiftGuildUser(
                        uid,
                        listGiftResource.stream().map(index -> GiftGuildInfo.create(index.id, EGuildGiftType.BUY, GiftGuildDescription.create(uid, objGet.productId), now + 86400)).collect(Collectors.toList()),
                        zone)){
            SendClaimIAPPackage objPut = new SendClaimIAPPackage(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        SendClaimIAPPackage objPut = new SendClaimIAPPackage();
        objPut.productId = idAPI;
        objPut.timeRefresh = IAPBuyManager.getInstance().getInfoIAPPackageUserModel(uid, idData, zone).readTimeRefresh(zone);
        send(objPut, user);

        //Event
        Map<String,Object> mapData = new HashMap<>();
        mapData.put(Params.ID, idAPI);
        GameEventAPI.ariseGameEvent(EGameEvent.CLAIM_IAP_PACKAGE, uid, mapData, getParentExtension().getParentZone());
    }



    /**
     * Lay item goi trong challenge
     * @param user
     * @param data
     */
    @WithSpan
    private void doClaimIAPChallenge(User user, ISFSObject data) {
        UserModel um = extension.getUserManager().getUserModel(user);
        long uid = um.userID;
        RecClaimIAPChallenge objGet = new RecClaimIAPChallenge(data);
        logger.info("s" + ((ZoneExtension) extension).getServerID() + "|send|" + objGet.data.toJson());
        UserIAPStoreModel userIAPStoreModel = IAPBuyManager.getInstance().getUserIAPStoreModel(uid, getParentExtension().getParentZone());
        UserIAPHomeModel userIAPHomeModel = IAPBuyManager.getInstance().getUserIAPHomeModel(uid, getParentExtension().getParentZone());
        String idData = objGet.productId;
        String idAPI = objGet.productId;

        //Chuyen doi idSale ---> id (Neu dang sale)
        InfoIAPSale dataIAPSale = IAPBuyManager.getInstance().getInfoIAPSale(uid, objGet.productId, getParentExtension().getParentZone());
        if(dataIAPSale != null) idData = dataIAPSale.id;

        //Kiem tra package co ton tai khong
        IAPChallengeVO challengeCf = IAPBuyManager.getInstance().getIAPListGetConfig(idData);
        if(challengeCf == null){
            SendClaimIAPChallenge objPut = new SendClaimIAPChallenge(ServerConstant.ErrorCode.ERR_NOT_EXSIST_PACKAGE);
            send(objPut, user);
            return;
        }

        //Kiem tra co the mua ko
        if(!IAPBuyManager.getInstance().canClaimIAPListGet(userIAPStoreModel, userIAPHomeModel, idData, objGet.point, EIAPClaimType.fromID(objGet.typeClaim), getParentExtension().getParentZone())){
            SendClaimIAPChallenge objPut = new SendClaimIAPChallenge(ServerConstant.ErrorCode.ERR_CAN_NOT_CLAIM_PACKAGE);
            send(objPut, user);
            return;
        }


        //Tuy theo kieu lay
        EIAPClaimType claimType = EIAPClaimType.fromID(objGet.typeClaim);
        switch (claimType){
            case ACTIVE_PREDIUM:
                //Server test khong can check thanh toan
                if(extension.isTestServer()){
                    //Save status vao database
                    if(!IAPBuyManager.getInstance().rewardIAPChallenge(userIAPStoreModel, userIAPHomeModel, idData, objGet.purchaseToken, objGet.point, EIAPClaimType.fromID(objGet.typeClaim), getParentExtension().getParentZone())){
                        SendClaimIAPChallenge objPut = new SendClaimIAPChallenge(ServerConstant.ErrorCode.ERR_SYS);
                        send(objPut, user);
                        return;
                    }
                    break;
                }
                //Active predium moi can tra tien
                if(IAPBuyManager.getInstance().haveCheckPaymentIAP(idData, getParentExtension().getParentZone())){

                    if(!IAPManager.getInstance().checkPaymentIAP(uid, EFlatform.fromStrValue(objGet.flatform), objGet.packageName, idAPI, objGet.purchaseToken, getParentExtension().getParentZone())){
                        SendClaimIAPChallenge objPut = new SendClaimIAPChallenge(ServerConstant.ErrorCode.ERR_NOT_HAVE_PAYMENT);
                        send(objPut, user);
                        return;
                    }

                    logger.info("s" + ((ZoneExtension) extension).getServerID() + "|" + "success|" + uid + "|" + um.accountID + "|" + objGet.productId + "|" + objGet.purchaseToken + "|" + objGet.transactionId);

                    switch (EFlatform.fromStrValue(objGet.flatform)){
                        case ANDROID:
                        case MENA:
                            //Luu vao sql
                            IAPPackageDAO.save(IAPPackageDBO.create(uid, idAPI, objGet.purchaseToken, objGet.transactionId, ServerConstant.IAPGate.GOOGLE_PLAY), getParentExtension().getParentZone());

                            //send to sdk
                            SDKGateAccount.logIAP(um.accountID, um.serverId, um.userID, ServerConstant.IAPGate.GOOGLE_PLAY, idAPI, objGet.purchaseToken, objGet.transactionId);

                            //Save status vao database
                            if(!IAPBuyManager.getInstance().rewardIAPChallenge(userIAPStoreModel, userIAPHomeModel, idData, objGet.purchaseToken, objGet.point, EIAPClaimType.fromID(objGet.typeClaim), getParentExtension().getParentZone())){
                                SendClaimIAPChallenge objPut = new SendClaimIAPChallenge(ServerConstant.ErrorCode.ERR_SYS);
                                send(objPut, user);
                                return;
                            }
                            break;
                        case IOS:
                            //Luu vao sql
                            IAPPackageDAO.save(IAPPackageDBO.create(uid, idAPI, objGet.transactionId, objGet.transactionId, ServerConstant.IAPGate.GOOGLE_PLAY), getParentExtension().getParentZone());

                            //send to sdk
                            SDKGateAccount.logIAP(um.accountID, um.serverId, um.userID, ServerConstant.IAPGate.GOOGLE_PLAY, idAPI, objGet.transactionId, objGet.transactionId);

                            //Save status vao database
                            if(!IAPBuyManager.getInstance().rewardIAPChallenge(userIAPStoreModel, userIAPHomeModel, idData, objGet.purchaseToken, objGet.point, EIAPClaimType.fromID(objGet.typeClaim), getParentExtension().getParentZone())){
                                SendClaimIAPChallenge objPut = new SendClaimIAPChallenge(ServerConstant.ErrorCode.ERR_SYS);
                                send(objPut, user);
                                return;
                            }
                            break;
                    }
                }
                break;
            case FREE:
            case PREDIUM:
            case FREE_PREDIUM:
            case ALL:
                //Them do do vao tui
                List<ResourcePackage> listResource = IAPBuyManager.getInstance().getResourceListGetCanClaim(uid, idData, claimType, objGet.point, getParentExtension().getParentZone());
                if(!BagManager.getInstance().addItemToDB(listResource, uid, getParentExtension().getParentZone(), UserUtils.TransactionType.DO_IAP_PACKAGE)){
                    SendClaimIAPChallenge objPut = new SendClaimIAPChallenge(ServerConstant.ErrorCode.ERR_SYS);
                    send(objPut, user);
                    return;
                }

                //Save status vao database
                if(!IAPBuyManager.getInstance().rewardIAPChallenge(userIAPStoreModel, userIAPHomeModel, idData, null, objGet.point, EIAPClaimType.fromID(objGet.typeClaim), getParentExtension().getParentZone())){
                    SendClaimIAPChallenge objPut = new SendClaimIAPChallenge(ServerConstant.ErrorCode.ERR_SYS);
                    send(objPut, user);
                    return;
                }
                break;
        }

        SendClaimIAPChallenge objPut = new SendClaimIAPChallenge();
        objPut.productId = idAPI;
        objPut.point = objGet.point;
        objPut.typeClaim = objGet.typeClaim;
        send(objPut, user);

        //Event
        Map<String,Object> mapData = new HashMap<>();
        mapData.put(Params.ID, idAPI);
        mapData.put(Params.TYPE, objGet.typeClaim);
        GameEventAPI.ariseGameEvent(EGameEvent.CLAIM_IAP_CHALLENGE, uid, mapData, getParentExtension().getParentZone());
    }

    /**
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doGetListIAPTabSpecial(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;
        UserIAPStoreModel userIAPStoreModel = IAPBuyManager.getInstance().getUserIAPStoreModel(uid, getParentExtension().getParentZone());
        UserIAPHomeModel userIAPHomeModel = IAPBuyManager.getInstance().getUserIAPHomeModel(uid, getParentExtension().getParentZone());

        SendGetListIAPTabSpecial objPut = new SendGetListIAPTabSpecial();
        objPut.userIAPStoreModel = userIAPStoreModel;
        objPut.userIAPHomeModel = userIAPHomeModel;
        objPut.zone = getParentExtension().getParentZone();
        objPut.listTabID = IAPBuyManager.getInstance().getListTabSpecial(uid, getParentExtension().getParentZone());
        objPut.listEventNoti = EventInGameManager.getInstance().getEventSpecial(getParentExtension().getParentZone());
        objPut.listPackageIDExile = IAPBuyManager.getInstance().getIAPSpecialPackageExiled(uid, getParentExtension().getParentZone());
        send(objPut, user);
    }
}
