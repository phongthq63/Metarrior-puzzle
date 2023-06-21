package com.bamisu.log.gameserver.module.user;

import com.bamisu.gamelib.config.AvatarConfig;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.base.Authenticator;
import com.bamisu.log.gameserver.datamodel.chat.UserChatModel;
import com.bamisu.log.gameserver.datamodel.guild.GuildModel;
import com.bamisu.log.gameserver.datamodel.hero.UserAllHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.UserBlockHeroModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.datamodel.nft.UserMintHeroModel;
import com.bamisu.log.gameserver.datamodel.nft.UserTokenModel;
import com.bamisu.log.gameserver.datamodel.user.UserSettingModel;
import com.bamisu.log.gameserver.entities.CustomErrorCode;
import com.bamisu.log.gameserver.entities.ExtensionClass;
import com.bamisu.log.gameserver.entities.LizClientDissconnectionReason;
import com.bamisu.gamelib.entities.MoneyType;
import com.bamisu.log.gameserver.module.GameEvent.BaseGameEvent;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.event.event.login14days.Login14DaysManager;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.bamisu.log.gameserver.module.nft.NFTManager;
import com.bamisu.log.gameserver.module.nft.cmd.send.SendNotifyMintHero;
import com.bamisu.log.gameserver.module.nft.defind.ETokenBC;
import com.bamisu.log.gameserver.module.pushnotify.PushNotifyHandler;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateAccount;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateGiftcode;
import com.bamisu.log.gameserver.module.sdkthriftclient.SDKGateMiltiServer;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.log.rabbitmq.RabbitMQManager;
import com.bamisu.log.sdkthrift.entities.TActiveGiftcodeResult;
import com.bamisu.log.sdkthrift.entities.TLinkAccountResult;
import com.bamisu.log.sdkthrift.entities.TLinkedAccount;
import com.bamisu.log.sdkthrift.entities.TSwitchAccountResult;
import com.bamisu.log.sdkthrift.exception.ThriftSVException;
import com.bamisu.gamelib.auth.LoginType;
import com.bamisu.log.gameserver.module.chat.defind.EChatType;
import com.bamisu.gamelib.email.EmailUtils;
import com.bamisu.gamelib.email.exception.SendingEmailTooFastException;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.log.gameserver.module.chat.ChatManager;
import com.bamisu.log.gameserver.module.user.cmd.receive.RecChangeChannelChat;
import com.bamisu.log.gameserver.module.user.cmd.send.SendChangeChannel;
import com.bamisu.log.gameserver.module.chat.config.entities.ChannelVO;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.user.cmd.receive.*;
import com.bamisu.log.gameserver.module.user.cmd.send.*;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.*;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.event.EEvent;
import com.bamisu.gamelib.model.DisplayNameModel;
import com.bamisu.gamelib.socialnetwork.ESocialNetwork;
import com.bamisu.log.gameserver.entities.CMDUtils;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.ValidateUtils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.gamelib.utils.socialcontroller.exceptions.SocialControllerException;
import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSConstants;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.exceptions.*;
import com.smartfoxserver.v2.extensions.ExtensionLogLevel;
import com.smartfoxserver.v2.util.ClientDisconnectionReason;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.web3j.crypto.WalletUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Popeye on 6/20/2017.
 */
public class UserHandler extends ExtensionBaseClientRequestHandler {

    public Logger logger = Logger.getLogger(UserHandler.class);
    public UserModuleLogic moduleLogic;
    public BaseGameEvent gameEvent;
    private final ScheduledExecutorService scheduledExecutorService;

    public UserHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_USER;
        logger.debug("test log UserHandler");
        moduleLogic = new UserModuleLogic(this);
        this.scheduledExecutorService = LizThreadManager.getInstance().getFixExecutorServiceByName(LizThreadManager.EThreadPool.NOTIFY, 1);
        initGameEvent();
    }

    private void initGameEvent() {
        gameEvent = new BaseGameEvent(getParentExtension().getParentZone()) {
            @Override
            public void handleGameEvent(EGameEvent event, long uid, Map<String, Object> data) {
                switch (event){
                    case LEVEL_USER_UPDATE:
                        UserModel userModel = ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(uid);
                        try {
                            SDKGateAccount.updateLevel(userModel.accountID, userModel.serverId, Integer.valueOf(String.valueOf(data.get(Params.LEVEL))));
                        } catch (TException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }

            @Override
            public void initEvent() {
                registerEvent(EGameEvent.LEVEL_USER_UPDATE);
            }
        };
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId) {
            case CMD.CMD_GET_PROFILE:
                handleGetProfile(user, data);
                break;
            case CMD.CMD_UPDATE_DNAME:
                handleUpdateDname(user, data);
                break;
            case CMD.CMD_UPDATE_GENDER:
                handleUpdateGender(user, data);
                break;
            case CMD.CMD_UPDATE_LANGUGE:
                handleUpdateLanguage(user, data);
                break;
            case CMD.CMD_UPDATE_STATUS_TEXT:
                handleUpdateStatusText(user, data);
                break;
            case CMD.CMD_UPDATE_AVATAR:
                handleUpdateAvatar(user, data);
                break;
            case CMD.CMD_UPDATE_PUSH_NOTIFICATION_SETTING:
                handlePushNotificationSetting(user, data);
                break;
            case CMD.CMD_GET_SETTING:
                handleGetSetting(user, data);
                break;
            case CMD.CMD_GET_SERVER_LIST:
                handleGetServerList(user, data);
                break;
            case CMD.CMD_GET_CHANNEL_LIST:
                doGetChannelList(user, data);
                break;
            case CMD.CMD_CHANGE_CHANNEL:
                doChangeChannel(user, data);
                break;
            case CMD.CMD_CHANGE_SERVER:
                handleChangeServer(user, data);
                break;
            case CMD.CMD_ACTIVE_GIFTCODE:
                handleActiveGiftcode(user, data);
                break;
            case CMD.CMD_LINK_ACCOUNT:
                handleLinkAccount(user, data);
                break;
            case CMD.CMD_SWITCH_ACCOUNT:
                handleSwitchAccount(user, data);
                break;
            case CMD.CMD_SEND_SUPORT:
                handleSendSuport(user, data);
                break;
            case CMD.CMD_UPDATE_STAGE:
                handleUpdateStage(user, data);
                break;
            case CMD.CMD_GET_STAGE:
                handleGetStage(user, data);
                break;
            case CMD.CMD_UPDATE_USERNAME:
                this.handleLinkUsername(user, data);
                break;
        }
    }

    @WithSpan
    private void handleGetStage(User user, ISFSObject data) {
        RecGetStage rec = new RecGetStage(data);
        rec.unpackData();
        SendGetStage send = new SendGetStage();
        UserModel um = extension.getUserManager().getUserModel(user);
        send.stage = um.stage;
        send.stageV2 = um.stageV3;
        send(send, user);
    }

    /**
     * update stage của tutorial
     * @param user
     * @param data
     */
    @WithSpan
    private void handleUpdateStage(User user, ISFSObject data) {
        RecUpdateStage rec = new RecUpdateStage(data);
        rec.unpackData();
        //Update stage
        UserModel um = extension.getUserManager().getUserModel(user);
        um.stage = rec.stage;
        um.stageV3 = new ArrayList<>();
        um.stageV3.addAll(rec.stageV2);
        if (um.saveToDB(user.getZone())){
            SendUpdateStage send = new SendUpdateStage();
            send(send, user);

//            switch (rec.stage.split(",")[1]){
//                case "4":   //đánh qua của đầu tiên
//                    Map statisticals = new HashMap<>();
//                    statisticals.put(Params.DAMAGE, 0);
//                    statisticals.put(Params.TANK, 0);
//                    statisticals.put(Params.HEAL, 0);
//                    CampaignManager.getInstance().completeCampaign(getParentExtension().getParentZone(), statisticals, true, um.userID, 0, 3, true);
//                    break;
//            }
            return;
        }
        SendUpdateStage send = new SendUpdateStage(ServerConstant.ErrorCode.ERR_SYS);
        send(send, user);
    }

    @WithSpan
    private void handleSendSuport(User user, ISFSObject data) {
        RecSuport recReport = new RecSuport(data);
        SendSuport sendSuport;

        if (!ValidateUtils.isEmail(recReport.email)) {
            sendSuport = new SendSuport(ServerConstant.ErrorCode.ERR_EMAIL_INVALID);
            send(sendSuport, user);
            return;
        }

        if (recReport.subtitle.length() > 50) {
            sendSuport = new SendSuport(ServerConstant.ErrorCode.ERR_MAIL_TITLE_INVALID);
            send(sendSuport, user);
            return;
        }

        if (recReport.content.length() < 16 || recReport.content.length() > 1000) {
            sendSuport = new SendSuport(ServerConstant.ErrorCode.ERR_MAIL_CONTENT_INVALID);
            send(sendSuport, user);
            return;
        }

        try {
            if (EmailUtils.getInstance().sendSubportMail(
                    ((ZoneExtension) getParentExtension()).getServerID(),
                    ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(user).userID,
                    recReport.email,
                    recReport.subtitle,
                    recReport.content)) {
                sendSuport = new SendSuport();
                send(sendSuport, user);
            } else {
                sendSuport = new SendSuport(ServerConstant.ErrorCode.ERR_Send_Email_FAIL);
                send(sendSuport, user);
            }
        } catch (SendingEmailTooFastException e) {
            e.printStackTrace();
            sendSuport = new SendSuport(ServerConstant.ErrorCode.ERRSendingEmailTooFastException);
            send(sendSuport, user);
        }
    }

    @WithSpan
    private void handleSwitchAccount(User user, ISFSObject data) {
        RecSwitchAccount recSwitchAccount = new RecSwitchAccount(data);
        TSwitchAccountResult switchAccountResult = SDKGateAccount.switchAccount(
                ESocialNetwork.fromIntValue(recSwitchAccount.socialNetwork),
                recSwitchAccount.token
        );

        SendSwitchAccount sendSwitchAccount = new SendSwitchAccount();
        sendSwitchAccount.addr = switchAccountResult.addr;
        sendSwitchAccount.port = switchAccountResult.port;
        sendSwitchAccount.zone = switchAccountResult.zone;
        sendSwitchAccount.loginKey = switchAccountResult.loginKey;
        send(sendSwitchAccount, user);
    }

    /**
     * liên kết tài khoản với mạng xã hội
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handleLinkAccount(User user, ISFSObject data) {
        RecLinkAccount recLinkAccount = new RecLinkAccount(data);
        UserModel userModel = ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(user);
        TLinkAccountResult linkAccountResult = null;
        SendLinkAccount sendLinkAccount;

        //call thrift API
        try {
            linkAccountResult = SDKGateAccount.linkAccount(
                    userModel.accountID,
                    userModel.userID,
                    ((ZoneExtension) getParentExtension()).getServerID(),
                    ESocialNetwork.fromIntValue(recLinkAccount.socialNetwork),
                    recLinkAccount.token
            );
        } catch (TException e) {
            e.printStackTrace();
            ThriftSVException thriftSVException = (ThriftSVException) e;
            switch (thriftSVException.errorCode) {
                case ServerConstant.ErrorCode.ERR_SYS:
                    sendLinkAccount = new SendLinkAccount(ServerConstant.ErrorCode.ERR_SYS);
                    send(sendLinkAccount, user);
                    return;
                case ServerConstant.ErrorCode.ACCOUNT_HAVE_LINKED:
                    sendLinkAccount = new SendLinkAccount(ServerConstant.ErrorCode.ACCOUNT_HAVE_LINKED);
                    send(sendLinkAccount, user);
                    return;
                case ServerConstant.ErrorCode.SOCIAL_ACCOUNT_HAVE_LINKED:
                    sendLinkAccount = new SendLinkAccount(ServerConstant.ErrorCode.SOCIAL_ACCOUNT_HAVE_LINKED);
                    send(sendLinkAccount, user);
                    return;
            }
        }

        sendLinkAccount = new SendLinkAccount();
        sendLinkAccount.socicalNetwork = linkAccountResult.socialNetwork;
        send(sendLinkAccount, user);

        //Event
        Map<String, Object> eventData = new HashMap<>();
        eventData.put(Params.SOCIAL_NETWORK, linkAccountResult.socialNetwork);
        eventData.put(Params.LIST, userModel.linked);
        GameEventAPI.ariseGameEvent(EGameEvent.LINK_ACCOUNT, userModel.userID, eventData, getParentExtension().getParentZone());
    }

    @WithSpan
    private void handleActiveGiftcode(User user, ISFSObject data) {
        UserModel userModel = ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(user);
        RecActiveGiftcode rec = new RecActiveGiftcode(data);

        SendActiveGiftcode send;

        if (!ValidateUtils.isGiftcode(rec.code)) {
            send = new SendActiveGiftcode(ServerConstant.ErrorCode.GIFTCODE_NOT_FOUND);
            send(send, user);
            return;
        }

        TActiveGiftcodeResult tActiveGiftcodeResult = null;
        try {
            tActiveGiftcodeResult = SDKGateGiftcode.activeGiftcode(rec.code, ((ZoneExtension) getParentExtension()).getServerID(), String.valueOf(userModel.userID), userModel.accountID);
        } catch (TException e) {
            e.printStackTrace();
            ThriftSVException thriftSVException = (ThriftSVException) e;
            send = new SendActiveGiftcode((short) thriftSVException.errorCode);
            send(send, user);
            return;
        }

        send = new SendActiveGiftcode();
//        send.pushGift(new ResourcePackage(MoneyType.GOLD.getId(), 1000)).pushGift(new ResourcePackage(MoneyType.DIAMOND.getId(), 1000)).pushGift(new ResourcePackage(SpecialItem.GOLD_2HOURS.getId(), 100));

//        String EID = BagManager.getInstance().getRandomWeapon().id;
//        send.pushGift(new EquipPackageVO(EID, 1));
        List<LinkedHashMap> hashMapsList = new ArrayList<>();
        List<ResourcePackage> resourcePackageList = new ArrayList<>();
        hashMapsList = Utils.fromJson(tActiveGiftcodeResult.gifts, hashMapsList.getClass());
        for (LinkedHashMap hashMap : hashMapsList) {
            if (hashMap.get("id") != null && hashMap.get("id").equals("BUSD")) {
                TokenResourcePackage resourcePackage = Utils.fromJson(Utils.toJson(hashMap), TokenResourcePackage.class);
                resourcePackageList.add(resourcePackage);
            } else {
                ResourcePackage resourcePackage = Utils.fromJson(Utils.toJson(hashMap), ResourcePackage.class);
                resourcePackageList.add(resourcePackage);
            }


        }
        BagManager.getInstance().addItemToDB(resourcePackageList, userModel.userID, getParentExtension().getParentZone(), UserUtils.TransactionType.ACTIVE_GIFT_CODE);
        send.pushGift(resourcePackageList);
        send(send, user);
    }

    @WithSpan
    private void handleChangeServer(User user, ISFSObject data) {
        RecChangeServer rec = new RecChangeServer(data);

        //get server info
        ServerInfo serverInfo;
        try {
            serverInfo = SDKGateMiltiServer.getServerInfo(rec.serverID);
        } catch (TException e) {
            e.printStackTrace();
            return;
        }

        SendChangeServer send = new SendChangeServer();
//        send.addr = serverInfo.addr;
        send.port = serverInfo.port;
        send.zone = serverInfo.zone;
        send(send, user);
    }

    @WithSpan
    private void handleGetServerList(User user, ISFSObject data) {
        UserModel userModel = ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(user);
        SendServerList send = new SendServerList();
        try {
            send.serverCount = SDKGateMiltiServer.getServerCount();
            send.joinedServerData = SDKGateMiltiServer.getJoinedServer(userModel.accountID);
        } catch (TException e) {
            send = new SendServerList(ServerConstant.ErrorCode.ERR_SYS);
            e.printStackTrace();
        }
        send(send, user);
    }

    @WithSpan
    private void handleGetSetting(User user, ISFSObject data) {
        UserSettingModel userSettingModel = UserSettingModel.copyFromDBtoObject(user.getName(), getParentExtension().getParentZone());
        if (userSettingModel != null) {
            SendSetting send = new SendSetting();
            send.pushNotificationSetting = userSettingModel.pushNotificationSetting;
            send(send, user);
        }
    }

    @WithSpan
    private void handlePushNotificationSetting(User user, ISFSObject data) {
        RecUpdatePushNotificationSetting rec = new RecUpdatePushNotificationSetting(data);
        UserSettingModel userSettingModel = UserSettingModel.copyFromDBtoObject(user.getName(), getParentExtension().getParentZone());
        if (userSettingModel != null) {
            if (rec.privateMessage != 2) userSettingModel.pushNotificationSetting.privateMessage = rec.privateMessage == 1;
            if (rec.allianceChat != 2) userSettingModel.pushNotificationSetting.allianceChat = rec.allianceChat == 1;
            if (rec.globalChat != 2) userSettingModel.pushNotificationSetting.globalChat = rec.globalChat == 1;
            if (rec.chanelChat != 2) userSettingModel.pushNotificationSetting.chanelChat = rec.chanelChat == 1;

            PushNotifyHandler pushNotifyHandler = (PushNotifyHandler) ((ZoneExtension) getParentExtension()).getServerHandler(Params.Module.MODULE_PUSH_NOTIFY);
            if(userSettingModel.pushNotificationSetting.globalChat){
                pushNotifyHandler.pushNotifyManager.turnOnGlobal(userSettingModel.uid);
            }else {
                pushNotifyHandler.pushNotifyManager.turnOffGlobal(userSettingModel.uid);
            }

            if(userSettingModel.pushNotificationSetting.chanelChat){
                pushNotifyHandler.pushNotifyManager.turnOnChanel(UserChatModel.copyFromDBtoObject(userSettingModel.uid, getParentExtension().getParentZone()).channelChat, userSettingModel.uid);
            }else {
                pushNotifyHandler.pushNotifyManager.turnOffChanel(UserChatModel.copyFromDBtoObject(userSettingModel.uid, getParentExtension().getParentZone()).channelChat, userSettingModel.uid);

            }

            userSettingModel.saveToDB(getParentExtension().getParentZone());
        }
    }

    @WithSpan
    private void handleUpdateAvatar(User user, ISFSObject data) {
        RecUpdateAvatar recUpdateAvatar = new RecUpdateAvatar(data);

//        UserModel userModel = ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(user);
        UserModel userModel = extension.getUserManager().getUserModel(user);
        boolean isChange = false;
        if (userModel.avatar != recUpdateAvatar.avatar) {
            userModel.avatar = recUpdateAvatar.avatar;
            isChange = true;
        }

        if (userModel.avatarFrame != recUpdateAvatar.frame) {
            userModel.avatarFrame = recUpdateAvatar.frame;
            isChange = true;
        }
        if(isChange){
            userModel.saveToDB(getParentExtension().getParentZone());
            try {
                SDKGateAccount.updateAvatar(userModel.accountID, userModel.serverId, userModel.avatar, userModel.avatarFrame);
            } catch (TException e) {
                e.printStackTrace();
            }
        }
    }

    @WithSpan
    private void handleUpdateStatusText(User user, ISFSObject data) {
        RecUpdateStatusText recUpdateStatusText = new RecUpdateStatusText(data);

        if (!ValidateUtils.isStatusText(recUpdateStatusText.content)) {
            SendUpdateStatusText sendUpdateStatusText = new SendUpdateStatusText(ServerConstant.ErrorCode.ERR_INVALID_STATUS_TEXT);
            send(sendUpdateStatusText, user);
            return;
        }

        UserModel userModel = ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(user);
        if (!userModel.statusText.equalsIgnoreCase(recUpdateStatusText.content)) {
            userModel.statusText = recUpdateStatusText.content;
            userModel.saveToDB(getParentExtension().getParentZone());
        }

        SendUpdateStatusText sendUpdateStatusText = new SendUpdateStatusText();
        sendUpdateStatusText.content = userModel.statusText;
        send(sendUpdateStatusText, user);
    }

    @WithSpan
    private void handleUpdateLanguage(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecUpdateLanguge recUpdateLanguge = new RecUpdateLanguge(data);

        if (!ValidateUtils.isLanguageID(recUpdateLanguge.languageID)) {
            SendUpdateLanguage sendUpdateLanguage = new SendUpdateLanguage(ServerConstant.ErrorCode.ERR_INVALID_LANGUAGE_ID);
            send(sendUpdateLanguage, user);
            return;
        }

        UserModel userModel = ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(user);
        if (!userModel.lang.equalsIgnoreCase(recUpdateLanguge.languageID)) {
            userModel.lang = recUpdateLanguge.languageID;
            userModel.saveToDB(getParentExtension().getParentZone());
        }

        SendUpdateLanguage sendUpdateLanguage = new SendUpdateLanguage();
        sendUpdateLanguage.languageID = userModel.lang;
        send(sendUpdateLanguage, user);

        //TODO: Join room chat nếu chưa có

        UserChatModel userChatModel = ChatManager.getInstance().getUserChatModel(uid, getParentExtension().getParentZone());
        if(userChatModel.readChannel().isEmpty()) {
            //Kiem tra config room theo language
            List<ChannelVO> channelCf = ChatManager.getInstance().getChannelConfigDependType(recUpdateLanguge.languageID);
            if (channelCf.isEmpty()) {
                SendUpdateLanguage objPut = new SendUpdateLanguage(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHANNEL);
                send(objPut, user);
                return;
            }

            List<Room> roomChannelIn = user.getJoinedRooms().stream().
                    filter(room -> room.getVariable(Params.TYPE).getIntValue().equals(EChatType.CHANNEL.getId())).
                    collect(Collectors.toList());
            List<Room> roomChannelLanguage = getParentExtension().getParentZone().getRoomListFromGroup(Params.Module.MODULE_CHAT).stream().
                    filter(room -> room.getVariable(Params.TYPE).getIntValue().equals(EChatType.CHANNEL.getId()) && room.getVariable(Params.LANGUAGE).getStringValue().equals(recUpdateLanguge.languageID)).
                    collect(Collectors.toList());
            //User khong the trong cung luc 2 channel
            if (roomChannelIn.size() > 1) {
                SendUpdateLanguage objPut = new SendUpdateLanguage(ServerConstant.ErrorCode.ERR_SYS);
                send(objPut, user);
                return;
            }

            Room roomJoin = null;
            for (Room room : roomChannelLanguage) {
                if (room.isFull()) continue;

                roomJoin = room;
                break;
            }

            //TH full toan bo room
            if (roomJoin == null) {
                ChannelVO newChannel = ChatManager.getInstance().addChannelManagerModel(channelCf.get(0), getParentExtension().getParentZone());

                CreateRoomSettings cfgRoomChannel = new CreateRoomSettings();
                cfgRoomChannel.setName(newChannel.id);
                cfgRoomChannel.setGroupId(Params.Module.MODULE_CHAT);
                cfgRoomChannel.setMaxUsers(newChannel.maxUser);
                cfgRoomChannel.setDynamic(true);
                cfgRoomChannel.setGame(false);
                cfgRoomChannel.setAutoRemoveMode(SFSRoomRemoveMode.NEVER_REMOVE);

                List<RoomVariable> listVariableChannel = new ArrayList<>();
                listVariableChannel.add(new SFSRoomVariable(Params.TYPE, EChatType.CHANNEL.getId()));
                listVariableChannel.add(new SFSRoomVariable(Params.NAME, newChannel.name));
                listVariableChannel.add(new SFSRoomVariable(Params.LANGUAGE, newChannel.type));
                cfgRoomChannel.setRoomVariables(listVariableChannel);

                cfgRoomChannel.setExtension(new CreateRoomSettings.RoomExtensionSettings(getParentExtension().getParentZone().getName(), ExtensionClass.CHAT_EXT));
                try {
                    roomJoin = ExtensionUtility.getInstance().createRoom(getParentExtension().getParentZone(), cfgRoomChannel, null, true, null);

                    System.out.println("------------------------------------------------------------------------------------------");
                    System.out.println(">>    Khởi tạo phòng chat chanel " + newChannel.id + " thành công    ...");
                    System.out.println("------------------------------------------------------------------------------------------");
                } catch (SFSCreateRoomException e) {
                    e.printStackTrace();

                    SendUpdateLanguage objPut = new SendUpdateLanguage(ServerConstant.ErrorCode.ERR_SYS);
                    send(objPut, user);
                    return;
                }
            }

            try {
                //Vao phong channel moi
                ExtensionUtility.getInstance().joinRoom(user, roomJoin);
                //Thoat phong channel hien tai
                if (!roomChannelIn.isEmpty()) ExtensionUtility.getInstance().leaveRoom(user, roomChannelIn.get(0));
                //Change room in data user
                ChatManager.getInstance().changeChannelChat(uid, roomJoin.getName(), getParentExtension().getParentZone());

            } catch (SFSJoinRoomException e) {
                e.printStackTrace();

                SendUpdateLanguage objPut = new SendUpdateLanguage(ServerConstant.ErrorCode.ERR_SYS);
                send(objPut, user);
            }
        }
    }

    /**
     * update giới tính
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handleUpdateGender(User user, ISFSObject data) {
        RecUpdateGender rec = new RecUpdateGender(data);
        rec.unpackData();

        SendUpdateGender sendUpdateGender;
        if (Gender.fromshortValue(rec.gender) == null) {
            sendUpdateGender = new SendUpdateGender(ServerConstant.ErrorCode.ERR_INVALID_GENDER);
            send(sendUpdateGender, user);
            return;
        }

        UserModel userModel = ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(user);

        //test
//        try {
//            Collection<VipData> vipData = SDKGateVip.addVip(userModel.accountID, Arrays.asList(new VipData(EVip.GOLD, 10000)));
//            Debug.info(Utils.toJson(vipData));
//        } catch (TException e) {
//            e.printStackTrace();
//        }

        if (userModel.gender != rec.gender) {
            userModel.gender = rec.gender;
            userModel.saveToDB(getParentExtension().getParentZone());
        }

        sendUpdateGender = new SendUpdateGender();
        sendUpdateGender.gender = rec.gender;
        send(sendUpdateGender, user);
    }

    /**
     * đổi tên hiển thị
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handleUpdateDname(User user, ISFSObject data) {
        RecUpdateDisplayName rec = new RecUpdateDisplayName(data);
        rec.unpackData();

        SendUpdateDisplayName send;


        if (!ValidateUtils.isDisplayName(rec.displayName)) {
            send = new SendUpdateDisplayName(ServerConstant.ErrorCode.ERR_INVALID_DNAME);
            send(send, user);
            return;
        }

        UserModel userModel = ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(user);

        //giống tên cũ
        if (rec.displayName.equals(userModel.displayName)) {
            return;
        }

        if (DisplayNameModel.copyFromDBtoObject(rec.displayName, getParentExtension().getParentZone()) != null) {
            send = new SendUpdateDisplayName(ServerConstant.ErrorCode.ERR_DNAME_ALREADY_EXIST);
            send(send, user);
            return;
        }


        ResourcePackage resource = Utils.isDefaultDisplayName(userModel.displayName) ? new ResourcePackage(MoneyType.DIAMOND.getId(), 0) : new ResourcePackage(ETokenBC.SOG.getId(), -100);
        if (resource.amount != 0) {
            if (!BagManager.getInstance().addItemToDB(
                    Collections.singletonList(resource), userModel.userID, getParentExtension().getParentZone(), UserUtils.TransactionType.CHANGE_DISPLAY_NAME
            )) {
                send = new SendUpdateDisplayName(ServerConstant.ErrorCode.ERR_NOT_ENOUGH_MONEY);
                send(send, user);
                return;
            }
        }

        //DisplayNameModel
//        DisplayNameModel displayNameModel = DisplayNameModel.copyFromDBtoObject(userModel.displayName, getParentExtension().getParentZone());
//        if (displayNameModel != null) {
//            displayNameModel.userID = -1;
//            displayNameModel.dName = "";
//            displayNameModel.saveToDB(getParentExtension().getParentZone());
//        }

        userModel.displayName = rec.displayName;
        DisplayNameModel.create(userModel, getParentExtension().getParentZone());
        userModel.saveToDB(getParentExtension().getParentZone());
        send = new SendUpdateDisplayName();
        send.displayName = rec.displayName;
        send(send, user);

        //update to SDK
        this.scheduledExecutorService.schedule(() -> {
            try {
                SDKGateAccount.updateDisplayName(userModel.accountID, userModel.serverId, userModel.displayName);
            } catch (TException e) {
                e.printStackTrace();
            }
        }, 5, TimeUnit.SECONDS);


        //event
        /*Map<String, Object> edata = new ConcurrentHashMap<>();
        edata.put(Params.EVENT, EEvent.ON_USER_CHANGE_DNAME_SUCCESS);
        edata.put(Params.UID, userModel.userID);
        edata.put(Params.USER_DISPLAY_NAME, userModel.displayName);
        ((ZoneExtension) getParentExtension()).getEventManager().onEvent(edata); */
        return;
    }


    /**
     * Thong tin danh sach channel
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doGetChannelList(User user, ISFSObject data) {
        List<Room> listRoom = getParentExtension().getParentZone().getRoomListFromGroup(Params.Module.MODULE_CHAT).parallelStream().
                filter(room -> room.getVariable(Params.TYPE).getIntValue().equals(EChatType.CHANNEL.getId())).
                collect(Collectors.toList());

        SendGetChannelList objPut = new SendGetChannelList();
        objPut.user = user;
        objPut.listRoom = listRoom;
        send(objPut, user);
    }


    /**
     * Vao room channel
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doChangeChannel(User user, ISFSObject data) {
        long uid = extension.getUserManager().getUserModel(user).userID;
        RecChangeChannelChat objGet = new RecChangeChannelChat(data);

        //Luu y channel trong config = room
        ChannelVO channelCf = ChatManager.getInstance().getListChannel(objGet.idChannel, getParentExtension().getParentZone());
        if (channelCf == null) {
            SendChangeChannel objPut = new SendChangeChannel(ServerConstant.ErrorCode.ERR_NOT_EXSIST_CHANNEL);
            send(objPut, user);
            return;
        }

        List<Room> roomChannelIn = getParentExtension().getParentZone().getRoomListFromGroup(Params.Module.MODULE_CHAT).
                parallelStream().
                filter(room -> room.containsUser(user) && room.getVariable(Params.TYPE).getIntValue().equals(EChatType.CHANNEL.getId())).
                collect(Collectors.toList());
        //User khong the trong cung luc 2 channel
        if (roomChannelIn.size() > 1) {
            SendChangeChannel objPut = new SendChangeChannel(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
            return;
        }

        Room roomJoin = getParentExtension().getParentZone().getRoomByName(objGet.idChannel);
        if (!roomChannelIn.isEmpty() && roomJoin.equals(roomChannelIn.get(0))) {
            SendChangeChannel objPut = new SendChangeChannel(ServerConstant.ErrorCode.ERR_ALREADY_IN_CHANNEL);
            send(objPut, user);
            return;
        }

        //Kiem tra dung loai room khong
        if (!roomJoin.getVariable(Params.TYPE).getIntValue().equals(EChatType.CHANNEL.getId())) {
            SendChangeChannel objPut = new SendChangeChannel(ServerConstant.ErrorCode.ERR_INVALID_CHANNEL);
            send(objPut, user);
            return;
        }

        //Kiem tra room day hay chua
        if (roomJoin.isFull()) {
            SendChangeChannel objPut = new SendChangeChannel(ServerConstant.ErrorCode.ERR_FULL_CHANNEL);
            send(objPut, user);
            return;
        }

        try {
            //Vao phong channel moi
            ExtensionUtility.getInstance().joinRoom(user, roomJoin);
            //Thoat phong channel hien tai
            if(!roomChannelIn.isEmpty()) ExtensionUtility.getInstance().leaveRoom(user, roomChannelIn.get(0));
            //Change room in data user
            ChatManager.getInstance().changeChannelChat(uid, objGet.idChannel, getParentExtension().getParentZone());

        } catch (SFSJoinRoomException e) {
            e.printStackTrace();
            return;
        }

        SendChangeChannel objPut = new SendChangeChannel();
        send(objPut, user);
    }

    /**
     * lấy thông tin màn hình profile (của mình)
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void handleGetProfile(User user, ISFSObject data) {
        SendProfile sendProfile = new SendProfile();
        sendProfile.zone = user.getZone();
        sendProfile.userModel = ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(user);
        GuildModel guildModel = GuildManager.getInstance().getGuildModelByUserID(sendProfile.userModel.userID, getParentExtension().getParentZone());
        if (guildModel != null) {
            sendProfile.alliance = guildModel.gName;
        }
        sendProfile.avatarList = AvatarConfig.getInstance().avatarList;

        if (sendProfile.userModel == null) {
            return;
        }

        sendProfile.primaryHeroes = HeroManager.getInstance().getTeamStrongestUserHeroModel(sendProfile.userModel.userID, getParentExtension().getParentZone());
        if (sendProfile.primaryHeroes == null) {
            return;
        }

        send(sendProfile, user);
    }

    @WithSpan
    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {
        switch (type) {
            case USER_LOGIN:
                handleLogin(event);
                break;
            case USER_LOGOUT:
                handleLogout(event);
                break;
            case USER_DISCONNECT:
                handleDisconnect(event);
                break;
        }
    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_USER, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addEventHandler(SFSEventType.USER_LOGIN, this);
        this.extension.addEventHandler(SFSEventType.USER_LOGOUT, this);
        this.extension.addEventHandler(SFSEventType.USER_DISCONNECT, this);

        this.extension.addServerHandler(Params.Module.MODULE_USER, this);
    }

    /**
     * @param event data {}
     */
    @WithSpan
    private void handleLogin(ISFSEvent event) throws SFSLoginException {
        //System.out.println("handleLogin 0");
        SFSObject data = (SFSObject) event.getParameter(SFSEventParam.LOGIN_IN_DATA);
        ISession session = (ISession) event.getParameter(SFSEventParam.SESSION);
        UserModel um = null;
        int cmdId = data.getInt(Params.CMD_ID);
        RecLogin cmd = new RecLogin(data);
        cmd.unpackData();
        switch (cmdId) {
            case CMD.CMD_LOGIN:
                um = handleLogin(data, session);
                break;
            case CMD.CMD_REGISTER:
                um = handleRegister(data);
                break;
        }

        ISFSObject outData = (ISFSObject) event.getParameter(SFSEventParam.LOGIN_OUT_DATA);
        //System.out.println("handleLogin 3");
        if (um != null) {
            outData.putUtfString(SFSConstants.NEW_LOGIN_NAME, String.valueOf(um.userID));
            //System.out.println("handleLogin 4");
            CMDUtils.putLoginData(cmd.action, outData, um, ((ZoneExtension) getParentExtension()).getServerID(), getParentExtension().getParentZone());
            //System.out.println("handleLogin 5");
            um.loginCount++;
            //Save time login - logout
            um.lastLogin = Utils.getTimestampInSecond();
            um.ip = session.getAddress();
            um.lastLogout = -1;
            um.saveToDB(getParentExtension().getParentZone());
            //Event for new player
        }
        //System.out.println("handleLogin 6");
    }

    @WithSpan
    private UserModel handleLogin(ISFSObject data, ISession session) throws SFSLoginException {
        //System.out.println("handleLogin 1");
        RecLogin cmd = new RecLogin(data);
        cmd.unpackData();
        UserModel userModel = null;
        SFSErrorData errorData = new SFSErrorData(CustomErrorCode.ERROR_SYSTEM);
        try {
            String ip = session.getAddress();
            if (ServerConstant.PRE_MAINTENANCE) {
                if(!ServerConstant.white_list_ip.contains(ip)){
                    throw new SocialControllerException(ServerConstant.ErrorCode.ERR_SERVER_PRE_MAINTENANCE, "Hệ thống đang bảo trì");
                }
            }

            LoginType loginType = LoginType.fromInt(cmd.loginType);
            ESocialNetwork socialNetwork = ESocialNetwork.fromIntValue(cmd.loginType);
            this.logger.error("Do login ");
            if (socialNetwork == ESocialNetwork.USERNAME) {
                userModel = Authenticator.doLogin(cmd.token, cmd.password, session.getAddress(), cmd.os, cmd.did, getParentExtension().getParentZone());
            } else {
                userModel = Authenticator.doLogin(cmd.token, loginType, session.getAddress(), cmd.os, cmd.did, getParentExtension().getParentZone());
            }

            // login success
            User sfsUser = ExtensionUtility.getInstance().getUserById(userModel.userID);
            UserModel finalUserModel = userModel;
            this.scheduledExecutorService.schedule(() -> {
                Login14DaysManager.getInstance().handleUserLogin(finalUserModel.userID, getParentExtension().getParentZone());
            }, 0, TimeUnit.SECONDS);
            if (sfsUser != null) {
                ExtensionUtility.getInstance().disconnectUser(sfsUser, ClientDisconnectionReason.KICK);
            }
            //System.out.println("handleLogin 2");
            return userModel;
        } catch (SocialControllerException e) {
            switch (e.error) {
                case ServerConstant.ErrorCode.ERR_USER_NOT_FOUND:
                    errorData.setCode(CustomErrorCode.ERR_USER_NOT_FOUND);
                    break;
                case ServerConstant.ErrorCode.ERR_INVALID_USERNAME_OR_PASSWORD:
                    errorData.setCode(CustomErrorCode.ERR_INVALID_USERNAME_OR_PASSWORD);
                    break;
                case ServerConstant.ErrorCode.ERR_SERVER_PRE_MAINTENANCE:
                    errorData.setCode(CustomErrorCode.ERROR_SERVER_MAINTENANCE);
                    break;
            }
            trace(ExtensionLogLevel.ERROR, "error: " + errorData.getCode());
            throw new SFSLoginException("error!", errorData);
        } catch (Exception e) {
            e.printStackTrace();
            trace(ExtensionLogLevel.ERROR, "error: " + errorData.getCode());
            throw new SFSLoginException("error system!", errorData);
        }
    }

    @WithSpan
    private UserModel handleRegister(ISFSObject data) {
        return null;
    }

    @WithSpan
    private void handleDisconnect(ISFSEvent event) {
        handlerSaveLogout(event);
    }

    @WithSpan
    private void handleLogout(ISFSEvent event) {
        handlerSaveLogout(event);
    }


    /**
     * Save thoi gian logout
     *
     * @param event
     */
    @WithSpan
    private void handlerSaveLogout(ISFSEvent event) {
        User user = (User) event.getParameter(SFSEventParam.USER);
        UserModel userModel = extension.getUserManager().getUserModel(user);
        userModel.lastLogout = Utils.getTimestampInSecond();
        userModel.saveToDB(getParentExtension().getParentZone());
        VipManager.getInstance().invalidateVipCache(userModel.accountID);
        //Xoa user trong room chat
        ChatManager.getInstance().removeUserFromChatRoom(userModel.userID, getParentExtension().getParentZone());
    }

    @WithSpan
    public ISFSObject GetLoginInfo(ISFSObject rec){
        String token = rec.getUtfString(Params.TOKEN);
        LoginType loginType = LoginType.fromInt(rec.getInt(Params.TYPE));
        String clientIP = rec.getUtfString(Params.IP);
        int marketId = 3;

        UserModel userModel;
        ISFSObject objPut = new SFSObject();
        try {
            userModel = Authenticator.doLogin(token, loginType, clientIP, marketId, "login", getParentExtension().getParentZone());
            if(userModel != null) {
                objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
                objPut.putUtfString(Params.MESS, "");
                objPut.putLong(Params.UID, userModel.userID);
            } else {
                objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_INVALID_TOKEN_LOGIN);
                objPut.putUtfString(Params.MESS, "Token login invalid.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            objPut.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_INVALID_TOKEN_LOGIN);
            objPut.putUtfString(Params.MESS, "Token login invalid.");
        }

        return objPut;
    }

    @WithSpan
    public ISFSObject updateUsername(ISFSObject req) {
        long userId = req.getLong(Params.UID);
        String username = req.getText(Params.USER_NAME);
        String password = req.getText(Params.USER_PASSWORD);
        String email = req.getText(Params.USER_EMAIL);
        String code = req.getText(Params.CODE);
        UserModel userModel = extension.getUserManager().getUserModel(userId);
        ISFSObject res = new SFSObject();
        Zone zone = getParentExtension().getParentZone();
        if (userModel == null) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_USER_NOT_FOUND);
            return res;
        }

        try {
            SDKGateAccount.updateUsernameAndPassword(userModel.accountID, username, password, email, code);
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        } catch (TException e) {
            ThriftSVException thriftSVException = (ThriftSVException) e;
            res.putShort(Params.ERROR_CODE, (short) thriftSVException.errorCode);
        }

        return res;
    }

    @WithSpan
    public ISFSObject changePassword(ISFSObject req) {
        long userId = req.getLong(Params.UID);
        String password = req.getText(Params.USER_PASSWORD);
        UserModel userModel = extension.getUserManager().getUserModel(userId);
        ISFSObject res = new SFSObject();
        if (userModel == null) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_USER_NOT_FOUND);
            return res;
        }

        try {
            SDKGateAccount.changePassword(userModel.accountID, password);
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        } catch (TException e) {
            ThriftSVException thriftSVException = (ThriftSVException) e;
            res.putShort(Params.ERROR_CODE, (short) thriftSVException.errorCode);
        }

        return res;
    }

    @WithSpan
    public ISFSObject getUserInfo(ISFSObject req) {
        long userId = req.getLong(Params.UID);
        UserModel userModel = extension.getUserManager().getUserModel(userId);
        Zone zone = getParentExtension().getParentZone();
        UserTokenModel userTokenModel = NFTManager.getInstance().getUserMineTokenModel(userId, zone);
        long sog = userTokenModel.readToken(ETokenBC.SOG);
        long mewa = userTokenModel.readToken(ETokenBC.MEWA);
        double busd = userTokenModel.readToken(ETokenBC.BUSD);
        logger.info("busd " + userTokenModel.readToken(ETokenBC.BUSD));
        logger.info("busd2 " + busd);
        // Lay slot hero
        UserAllHeroModel userAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(userId, zone);
        int currentSlot = userAllHeroModel.readSizeBagHero(zone);
        String wallet = "";
        String username = "";
        String email = "";
        if (userModel != null) {
            try {
                Map<String, String> linkedAccount = SDKGateAccount.getLinkedAccount(userModel.accountID);
                wallet = linkedAccount.getOrDefault(Params.WALLET, "");
                username = linkedAccount.getOrDefault(Params.USER_NAME, "");
                email = linkedAccount.getOrDefault(Params.USER_EMAIL, "");
            } catch (TException e) {
                e.printStackTrace();
            }
        }

        ISFSObject res = new SFSObject();
        ISFSObject data = new SFSObject();
        data.putLong("sog", sog);
        data.putLong("mewa", mewa);
        data.putDouble("busd", busd);
        data.putText(Params.USER_NAME, username);
        data.putText(Params.WALLET, wallet);
        data.putText(Params.USER_EMAIL, email);
        data.putInt("maxSlot", currentSlot);
        res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        res.putSFSObject(Params.DATA, data);
        return res;
    }

    @WithSpan
    public ISFSObject linkWallet(ISFSObject req) {
        long uid = req.getLong(Params.UID);
        String wallet = req.getText(Params.WALLET);
        String username = req.getText(Params.USER_NAME);
        String password = req.getText(Params.USER_PASSWORD);
        ISFSObject res = new SFSObject();
        Zone zone = getParentExtension().getParentZone();
        if (!WalletUtils.isValidAddress(wallet)) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_WALLET_ADDRESS_INVALID);
            return res;
        }

        UserModel userModel = extension.getUserManager().getUserModel(uid);
        if (userModel == null) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_USER_NOT_FOUND);
            return res;
        }

        try {
            long newUid = SDKGateAccount.linkWallet(userModel.accountID, wallet, username, password, Integer.parseInt(zone.getName()));
            if (newUid > -1) {
                UserAllHeroModel fromAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(uid, zone);
                UserAllHeroModel toAllHeroModel = HeroManager.getInstance().getUserAllHeroModel(newUid, zone);
                List<HeroModel> heroes = new ArrayList<>();
                for (HeroModel heroModel : fromAllHeroModel.listAllHeroModel) {
                    if (heroModel.type == EHeroType.NFT.getId()) {
                        heroes.add(heroModel);
                    }
                }

                if (heroes.size() > 0) {
                    toAllHeroModel.listAllHeroModel.addAll(heroes);
                    toAllHeroModel.saveToDB(zone);
                }

                UserMintHeroModel fromUserMintHeroModel = NFTManager.getInstance().getUserMintHeroModel(uid, zone);
                UserMintHeroModel toUserMintHeroModel = NFTManager.getInstance().getUserMintHeroModel(newUid, zone);
                boolean isUpdate = false;
                if (fromUserMintHeroModel.listHeroMint.size() > 0) {
                    toUserMintHeroModel.listHeroMint.addAll(fromUserMintHeroModel.listHeroMint);
                    isUpdate = true;
                }

                if (fromUserMintHeroModel.listHeroMine.size() > 0) {
                    toUserMintHeroModel.listHeroMine.addAll(fromUserMintHeroModel.listHeroMine);
                    isUpdate = true;
                }

                if (isUpdate) {
                    toUserMintHeroModel.saveToDB(zone);
                }

                UserBlockHeroModel fromUserBlockHeroModel = HeroManager.getInstance().getUserBlockHeroModel(uid, zone);
                UserBlockHeroModel toUserBlockHeroModel = HeroManager.getInstance().getUserBlockHeroModel(newUid, zone);
                if (fromUserBlockHeroModel.listHeroModel.size() > 0) {
                    toUserBlockHeroModel.listHeroModel.addAll(fromAllHeroModel.listAllHeroModel);
                    toUserBlockHeroModel.saveToDB(zone);
                }


            }

            SendNotifyMintHero packet = new SendNotifyMintHero(uid);
            User user = ExtensionUtility.getInstance().getUserById(newUid);
            if (user != null) {
                send(packet, user);
            }
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
            res.putLong(Params.UID, newUid);
        } catch (TException e) {
            ThriftSVException thriftSVException = (ThriftSVException) e;
            res.putShort(Params.ERROR_CODE, (short) thriftSVException.errorCode);
        }

        return res;
    }

    public ISFSObject resetRmq() {
        ISFSObject res = new SFSObject();
        res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.NONE);
        RabbitMQManager.resetConnection();
        return res;
    }

    private void handleLinkUsername(User user, ISFSObject data) {
        UserModel userModel = ((ZoneExtension) getParentExtension()).getUserManager().getUserModel(user);
        if (userModel == null) {
            return;
        }

        RecLinkUsername rec = new RecLinkUsername(data);
        short errorCode = ServerConstant.ErrorCode.NONE;
        try {
            SDKGateAccount.updateUsernameAndPassword(userModel.accountID, rec.username, rec.password, rec.email, rec.code);
        } catch (TException e) {
            ThriftSVException thriftSVException = (ThriftSVException) e;
            errorCode = (short) thriftSVException.errorCode;
        }

        SendLinkUsername packet = new SendLinkUsername(errorCode);
        this.send(packet, user);
    }

}
