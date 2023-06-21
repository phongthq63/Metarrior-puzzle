package com.bamisu.log.gameserver;

import com.bamisu.gamelib.ExtensionHandleInternalMessage;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.event.EEvent;
import com.bamisu.gamelib.event.EventHandler;
import com.bamisu.gamelib.event.EventManager;
import com.bamisu.gamelib.event.IEventer;
import com.bamisu.gamelib.iap.IOSIAPVerify;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.base.OnServerReadyHandler;
import com.bamisu.log.gameserver.entities.ExtensionClass;
import com.bamisu.log.gameserver.gamethriftserver.ThriftServer;
import com.bamisu.log.gameserver.module.IAPBuy.IAPBuyHandler;
import com.bamisu.log.gameserver.module.WoL.WoLHandler;
import com.bamisu.log.gameserver.module.adventure.AdventureHandler;
import com.bamisu.log.gameserver.module.arena.ArenaHandler;
import com.bamisu.log.gameserver.module.bag.BagHandler;
import com.bamisu.log.gameserver.module.bot.BotManager;
import com.bamisu.log.gameserver.module.campaign.CampaignHandler;
import com.bamisu.log.gameserver.module.campaign.config.MainCampaignConfig;
import com.bamisu.log.gameserver.module.celestial.CelestialHandler;
import com.bamisu.log.gameserver.module.characters.CharacterHandler;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.chat.ChatManager;
import com.bamisu.log.gameserver.module.chat.ChatManagerHandler;
import com.bamisu.log.gameserver.module.chat.config.entities.ChannelVO;
import com.bamisu.log.gameserver.module.chat.defind.EChatType;
import com.bamisu.log.gameserver.module.darkgate.DarkGateHandler;
import com.bamisu.log.gameserver.module.event.EventInGameHandler;
import com.bamisu.log.gameserver.module.friends.FriendHandler;
import com.bamisu.log.gameserver.module.guild.GuildHandler;
import com.bamisu.log.gameserver.module.hero.HeroHandler;
import com.bamisu.log.gameserver.module.hunt.HuntHandler;
import com.bamisu.log.gameserver.module.ingame.FightingHandler;
import com.bamisu.log.gameserver.module.invite.InviteHandler;
import com.bamisu.log.gameserver.module.item.ItemHandler;
import com.bamisu.log.gameserver.module.league.LeagueHandler;
import com.bamisu.log.gameserver.module.league.LeagueManager;
import com.bamisu.log.gameserver.module.lucky.LuckyHandler;
import com.bamisu.log.gameserver.module.lucky_draw.LuckyDrawHandler;
import com.bamisu.log.gameserver.module.mage.MageHandler;
import com.bamisu.log.gameserver.module.mail.MailHandler;
import com.bamisu.log.gameserver.module.mission.MissionHandler;
import com.bamisu.log.gameserver.module.nft.NFTHandler;
import com.bamisu.log.gameserver.module.notification.NotificationHandler;
import com.bamisu.log.gameserver.module.pushnotify.PushNotifyHandler;
import com.bamisu.log.gameserver.module.quest.QuestHandler;
import com.bamisu.log.gameserver.module.social.SocialHandler;
import com.bamisu.log.gameserver.module.store.StoreHandler;
import com.bamisu.log.gameserver.module.tower.TowerHandler;
import com.bamisu.log.gameserver.module.user.UserHandler;
import com.bamisu.log.gameserver.module.vip.VipHandler;
import com.bamisu.log.gameserver.sql.guild.dao.GuildDAO;
import com.bamisu.log.gameserver.sql.lucky.dao.LuckyDAO;
import com.bamisu.log.gameserver.sql.luckydraw.dao.LuckyDrawDAO;
import com.bamisu.log.gameserver.sql.user.dao.CcuDAO;
import com.bamisu.log.nft.Web3jFactory;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ZoneExtension extends BaseExtension implements IEventer {
    EventManager eventManager = new EventManager();
    ExtensionHandleInternalMessage handleInternalMessage = new ZoneHandleInternalMessage(this);
    private ZoneCacheData zoneCacheData;
    private BotManager botManager;

    @WithSpan
    @Override
    public void init() {
        trace(" extension init!!!!");
        addEventHandler(SFSEventType.SERVER_READY, OnServerReadyHandler.class);
    }

    @WithSpan
    @Override
    public void onServerReady() {
        trace("onServerReady");
        initLogger();
        initConfig();
        initCacheServer();
        initDB();
        initLogic();
        initModule();
        initTest();

        initRoomChat();
        MainCampaignConfig.getInstance();

        ThriftServer.getInstance().start();
//        SDKConnection sdkConnection = new SDKConnection(this);
    }

    @WithSpan
    private void initCacheServer() {
        zoneCacheData = new ZoneCacheData(getParentZone());
    }

    @WithSpan
    private void initRoomChat() {
        CreateRoomSettings cfgRoomGlobal = new CreateRoomSettings();
        cfgRoomGlobal.setName("global");
        cfgRoomGlobal.setGroupId(Params.Module.MODULE_CHAT);
        cfgRoomGlobal.setMaxUsers(100000);
        cfgRoomGlobal.setDynamic(true);
        cfgRoomGlobal.setGame(false);
        cfgRoomGlobal.setAutoRemoveMode(SFSRoomRemoveMode.NEVER_REMOVE);

        List<RoomVariable> listVariableGlobal = new ArrayList<>();
        listVariableGlobal.add(new SFSRoomVariable(Params.TYPE, EChatType.GLOBAL.getId()));
        listVariableGlobal.add(new SFSRoomVariable(Params.NAME, "global"));

        cfgRoomGlobal.setRoomVariables(listVariableGlobal);
        cfgRoomGlobal.setExtension(new CreateRoomSettings.RoomExtensionSettings(getParentZone().getName(), ExtensionClass.CHAT_EXT));
        try {
            ExtensionUtility.getInstance().createRoom(getParentZone(), cfgRoomGlobal, null, true, null);

            System.out.println("------------------------------------------------------------------------------------------");
            System.out.println(">>    Khởi tạo phòng chat global thành công    ...");
            System.out.println("------------------------------------------------------------------------------------------");
        } catch (SFSCreateRoomException e) {
            e.printStackTrace();
        }

        for (ChannelVO channel : ChatManager.getInstance().getListChannel(getParentZone())) {
            CreateRoomSettings cfgRoomChannel = new CreateRoomSettings();
            cfgRoomChannel.setName(channel.id);
            cfgRoomChannel.setGroupId(Params.Module.MODULE_CHAT);
            cfgRoomChannel.setMaxUsers(channel.maxUser);
            cfgRoomChannel.setDynamic(true);
            cfgRoomChannel.setGame(false);
            cfgRoomChannel.setAutoRemoveMode(SFSRoomRemoveMode.NEVER_REMOVE);

            List<RoomVariable> listVariableChannel = new ArrayList<>();
            listVariableChannel.add(new SFSRoomVariable(Params.TYPE, EChatType.CHANNEL.getId()));
            listVariableChannel.add(new SFSRoomVariable(Params.NAME, channel.name));
            listVariableChannel.add(new SFSRoomVariable(Params.LANGUAGE, channel.type));
            cfgRoomChannel.setRoomVariables(listVariableChannel);

            cfgRoomChannel.setExtension(new CreateRoomSettings.RoomExtensionSettings(getParentZone().getName(), ExtensionClass.CHAT_EXT));
            try {
                ExtensionUtility.getInstance().createRoom(getParentZone(), cfgRoomChannel, null, true, null);

                System.out.println("------------------------------------------------------------------------------------------");
                System.out.println(">>    Khởi tạo phòng chat chanel " + channel.id + " thành công    ...");
                System.out.println("------------------------------------------------------------------------------------------");
            } catch (SFSCreateRoomException e) {
                e.printStackTrace();
            }
        }
    }

    @WithSpan
    private void initTest() {
    }

    @WithSpan
    @Override
    public void initLogger() {
        trace("initLogger");
    }

    @WithSpan
    @Override
    public void initConfig() {
        trace("initConfig");
        CharactersConfigManager.getInstance();
        IOSIAPVerify.getInstance();
//        Web3jFactory.getInstance();
        setTestServer(Boolean.parseBoolean(getConfigProperties().getProperty("is_test_server")));
    }

    @WithSpan
    @Override
    public void initDB() {
        trace("initDB");

        // couchbase
        try {
            getDataController().getController().set(getParentZone().getName() + "_initDB", String.valueOf(Utils.getTimestampInSecond()));
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        //ping to couchbase
        getApi().getNewScheduler(1).scheduleAtFixedRate(() -> {
            try {
                getDataController().getController().set(getParentZone().getName() + "_ping", Utils.getTimestampInSecond());
            } catch (DataControllerException e) {
                e.printStackTrace();
            }
        }, 5, 30, TimeUnit.SECONDS);

        // sql
        getSQLController();
        GuildDAO.startThreadSaveGuild(getParentZone());
//        MoneyChangeDAO.startThreadSaveMoney(getParentZone());
        LuckyDAO.startThreadGenerateLuckyNumber(getParentZone());
        LuckyDrawDAO.startThreadCreateHistoryRankTopUser(getParentZone());
//        IAPPackageDAO.startThreadSaveIAP(getParentZone());
        CcuDAO.startThreadSaveCCU(getParentZone());

        //Redisson
//        getRedisController();
    }

    @WithSpan
    @Override
    public void initLogic() {
        trace("initLogic");
    }

    @WithSpan
    @Override
    public void initModule() {
        getUserManager();
        new ZoneHandle(this);
        new UserHandler(this);
        new CharacterHandler(this);
        new ItemHandler(this);
        new HeroHandler(this);
        new BagHandler(this);
        new CampaignHandler(this);
        new MageHandler(this);
        new AdventureHandler(this);
        new GuildHandler(this);
        new CelestialHandler(this);
        new MissionHandler(this);
        new MailHandler(this);
        new StoreHandler(this);
        new IAPBuyHandler(this);
        new SocialHandler(this);
        new FriendHandler(this);
        new VipHandler(this);
        new HuntHandler(this);
        new ChatManagerHandler(this);
        new TowerHandler(this);
        new QuestHandler(this);
        new WoLHandler(this);
        new InviteHandler(this);
        new EventInGameHandler(this);
        new NotificationHandler(this);
        new DarkGateHandler(this);
        new ArenaHandler(this);
        new PushNotifyHandler(this);
        new NFTHandler(this);
        new LuckyHandler(this);
        new LuckyDrawHandler(this);
        new LeagueHandler(this);
        new FightingHandler(this);
        //init bot
        botManager = new BotManager(this);


        //test rollback data
//        try {
//            UserBagModel userBagModel = null;
//            int MON1018 = 0;
//            int MON1001 = 0;
//            int MON1002 = 0;
//            int MON1003 = 0;
//            int MON1019 = 0;
//
//            for (long uid = 1000000; uid <= 1000275; uid++) {
//                //MON1018
//                if (getUserManager().getUserModel(uid) == null) continue;
//                userBagModel = BagManager.getInstance().getUserBagModel(uid, getParentZone());
//                if (userBagModel != null) {
//                    List<MoneyPackageVO> listChange = new ArrayList<>();
//                    MON1018 = Math.toIntExact(userBagModel.readMoney(MoneyType.SAGE_EXP, getParentZone()));
//                    if (MON1018 > 2173000) {
//                        listChange.add(new MoneyPackageVO(MoneyType.SAGE_EXP, 2173000 - MON1018));
//                    }
//
//                    MON1001 = Math.toIntExact(userBagModel.readMoney(MoneyType.GOLD, getParentZone()));
//                    if (MON1001 > 2000000) {
//                        listChange.add(new MoneyPackageVO(MoneyType.GOLD, 2000000 - MON1001));
//                    }
//
//                    MON1002 = Math.toIntExact(userBagModel.readMoney(MoneyType.MERITS, getParentZone()));
//                    if (MON1002 > 2000000) {
//                        listChange.add(new MoneyPackageVO(MoneyType.MERITS, 2000000 - MON1002));
//                    }
//
//                    MON1003 = Math.toIntExact(userBagModel.readMoney(MoneyType.ESSENCE, getParentZone()));
//                    if (MON1003 > 1000) {
//                        listChange.add(new MoneyPackageVO(MoneyType.ESSENCE, 1000 - MON1003));
//                    }
//
//                    MON1019 = Math.toIntExact(userBagModel.readMoney(MoneyType.MIRAGE_ESSENCE, getParentZone()));
//                    if (MON1019 > 1000) {
//                        listChange.add(new MoneyPackageVO(MoneyType.MIRAGE_ESSENCE, 1000 - MON1019));
//                    }
//
//                    //trừ và đền bù
//                    if (!listChange.isEmpty()) {
//                        userBagModel.changeMoney(listChange, UserUtils.TransactionType.FIX_BUG, getParentZone());
//                    }
//                    MailUtils.getInstance().sendMailFixBugAFK(uid, Arrays.asList(new ResourcePackage(MoneyType.DIAMOND.getId(), 5000)), getParentZone());
//                        int mailIndex = -1;
//                        MailModel mailModel = MailModel.copyFromDBtoObject(uid, getParentZone());
//                        for(MailVO mailVO : mailModel.listMail){
//                            if(mailVO.listGift.size() == 3){
//                                if(mailVO.listGift.get(0).amount == 100){
//                                    mailIndex = mailModel.listMail.indexOf(mailVO);
//                                    break;
//                                }
//                            }
//                        }
//
//                        if(mailIndex != -1){
//                            mailModel.listMail.remove(mailIndex);
//                            mailModel.saveToDB(getParentZone());
//                            System.out.println(uid +" false ");
//                        }
//
//                    }
//                System.out.println("done");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @WithSpan
    @Override
    public Object handleInternalMessage(String cmdName, Object params) {
        return handleInternalMessage.handleInternalMessage(cmdName, params);
    }

    public int getServerID() {
        return Integer.parseInt(getParentZone().getName());
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public void registerEvent(EventHandler eventHandler, EEvent event) {
        getEventManager().regiter(eventHandler, event);
    }

    public ZoneCacheData getZoneCacheData() {
        return zoneCacheData;
    }

    public BotManager getBotManager() {
        return botManager;
    }
}
