package com.bamisu.log.gameserver;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.gamelib.utils.PushNotifyUtils;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.datamodel.bag.entities.MissionDetail;
import com.bamisu.log.gameserver.datamodel.chat.UserChatModel;
import com.bamisu.log.gameserver.datamodel.guild.GuildModel;
import com.bamisu.log.gameserver.datamodel.pushnotify.UserPushNotifyModel;
import com.bamisu.log.gameserver.entities.ExtensionClass;
import com.bamisu.log.gameserver.module.adventure.entities.AFKDetail;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.chat.ChatManager;
import com.bamisu.log.gameserver.module.chat.config.entities.ChannelVO;
import com.bamisu.log.gameserver.module.chat.defind.EChatType;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.log.gameserver.module.guild.config.entities.GuildVO;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSJoinRoomException;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ZoneHandle extends ExtensionBaseClientRequestHandler {

    private Logger logger = Logger.getLogger(ZoneHandle.class);    //Get log smartfox
    private final ScheduledExecutorService schedulePushNotifyAfk = LizThreadManager.getInstance().getFixExecutorServiceByName("pushnotify", 1);
    private final ScheduledExecutorService schedulePushNotifyMission = LizThreadManager.getInstance().getFixExecutorServiceByName("pushnotify", 1);

    public ZoneHandle(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.ZONE;
        this.initScheduler();
        this.initSchedulerMisson();
        this.startThreadClearRoom();
    }

    @WithSpan
    private void initSchedulerMisson() {
        schedulePushNotifyMission.scheduleAtFixedRate(() -> {
            try {
                Map<Long, MissionDetail> mapping = ((ZoneExtension) this.extension.getParentZone().getExtension()).getZoneCacheData().getMissionDetailMap();
                List<MissionDetail> listPush = new ArrayList<>();
                synchronized (mapping) {
                    for (MissionDetail missionDetail : mapping.values()) {
                        if (ExtensionUtility.getInstance().getUserById(missionDetail.uid) == null) { //check k online
                            UserBagModel userBagModel = BagManager.getInstance().getUserBagModel(missionDetail.uid, getParentExtension().getParentZone());
                            int energy = BagManager.getInstance().getEnergyInfo(userBagModel, getParentExtension().getParentZone()).point;
                            if(energy == 100 && (missionDetail.maxTime <= Utils.getTimestampInSecond() - missionDetail.lastTime)){
                                listPush.add(missionDetail);
                            }
                        }
                    }

                    if (!listPush.isEmpty()) {
                        ((ZoneExtension) this.extension.getParentZone().getExtension()).getZoneCacheData().removeMissionDetail(listPush);
                        List<String> keys = new ArrayList<>();
                        for (MissionDetail missionDetail : listPush) {
                            keys.addAll(UserPushNotifyModel.copyFromDBtoObject(missionDetail.uid, getParentExtension().getParentZone()).getAllKeys());
                        }
                        PushNotifyUtils.push(keys, "pn004");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 60, TimeUnit.SECONDS);

    }

    @WithSpan
    private void initScheduler() {
        schedulePushNotifyAfk.scheduleAtFixedRate(() -> {
            try {
                Map<Long, AFKDetail> mapping = ((ZoneExtension) this.extension.getParentZone().getExtension()).getZoneCacheData().getAFKDetailMap();
                List<AFKDetail> listPush = new ArrayList<>();
                synchronized (mapping) {
                    for (AFKDetail afkDetail : mapping.values()) {
                        if (ExtensionUtility.getInstance().getUserById(afkDetail.uid) == null) { //check k online
                            if (afkDetail.maxTime <= (Utils.getTimestampInSecond() - afkDetail.rewardTime)) { //full time
                                listPush.add(afkDetail);
                            }
                        }
                    }

                    if (!listPush.isEmpty()) {
                        ((ZoneExtension) this.extension.getParentZone().getExtension()).getZoneCacheData().removeAFKDetail(listPush);
                        List<String> keys = new ArrayList<>();
                        for (AFKDetail afkDetail : listPush) {
                            keys.addAll(UserPushNotifyModel.copyFromDBtoObject(afkDetail.uid, getParentExtension().getParentZone()).getAllKeys());
                        }
                        PushNotifyUtils.push(keys, "pn001");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    /**
     *
     */
    private void startThreadClearRoom() {
        getParentExtension().getParentZone().getRoomListFromGroup("game")
                .forEach(room -> {
                    if (room.getSize().getUserCount() <= 0) {
                        ExtensionUtility.getInstance().removeRoom(room);
                    }
                });
        System.gc();

        LizThreadManager.getInstance().getFixExecutorServiceByName(LizThreadManager.EThreadPool.ROOM, 1).schedule(this::startThreadClearRoom, 1, TimeUnit.MINUTES);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId) {
            case CMD.CMD_PING:
                System.out.println("ping - " + user);
                break;
        }
    }

    @WithSpan
    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {
        switch (type) {
            case USER_JOIN_ZONE:
                onUserJoinZone(event);
                break;
        }
    }

    @WithSpan
    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.ZONE, this);
    }

    @WithSpan
    @Override
    protected void initHandlerServerEvent() {
        this.extension.addEventHandler(SFSEventType.USER_JOIN_ZONE, this);

        this.extension.addServerHandler(Params.ZONE, this);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * Day user vao phong chat
     *
     * @param event
     */
    @WithSpan
    private void onUserJoinZone(ISFSEvent event) throws SFSJoinRoomException {
        //Vao room
        handlerJoinRoomChat(event);

        User user = (User) event.getParameter(SFSEventParam.USER);
        UserModel um = extension.getUserManager().getUserModel(user);

        //update list Login
        extension.getUserManager().onUserLogin(um.userID);

        //update mail
        VipManager.getInstance().updateGiftFromMail(um.userID, user.getZone());

        //update push notify gift guild
        GuildManager.getInstance().onUserLogin(um.userID, user.getZone());

        //update bao hiem summon
        HeroManager.SummonManager.getInstance().updateGuaranteedSummon(um.userID, user.getZone());
    }


    /*----------------------------------------------------------------------------------------------------------------*/
    @WithSpan
    private void handlerJoinRoomChat(ISFSEvent event) throws SFSJoinRoomException {
        User user = (User) event.getParameter(SFSEventParam.USER);
        long uid = extension.getUserManager().getUserModel(user).userID;
        Room roomJoin;

        // vao room chat global
        ExtensionUtility.getInstance().joinRoom(user, getParentExtension().getParentZone().getRoomByName("global"));

        // Vao room chat channel
        UserChatModel userChatModel = ChatManager.getInstance().getUserChatModel(uid, getParentExtension().getParentZone());
        if (userChatModel.readChannel() != null && !userChatModel.readChannel().isEmpty()) {
            ChannelVO channelUser = ChatManager.getInstance().getListChannel(userChatModel.readChannel(), getParentExtension().getParentZone());
            roomJoin = getParentExtension().getParentZone().getRoomByName(userChatModel.readChannel());
            if (roomJoin != null && !roomJoin.isFull()) {
                ExtensionUtility.getInstance().joinRoom(user, roomJoin);
            } else {

                //Duyet mang room config
                int indexRoom = 0;
                List<ChannelVO> listChannel = ChatManager.getInstance().getListChannel(getParentExtension().getParentZone());
                ChannelVO channelCf;
                while (true) {
                    channelCf = listChannel.get(indexRoom);
                    roomJoin = getParentExtension().getParentZone().getRoomByName(channelCf.id);

                    //Neu cac room hien co ton tai va chua day -> add user vao room
                    if (roomJoin != null && !roomJoin.isFull()) {
                        try {
                            ExtensionUtility.getInstance().joinRoom(user, roomJoin);
                            break;
                        } catch (SFSJoinRoomException e) {
                            System.out.println();
                            System.out.println("----------------     Phòng channel user create đã full     --------------------");
                            System.out.println();
                        }
                    }

                    //Truong hop full toan bo room da co
                    if (listChannel.size() - 1 <= indexRoom) {
                        //Tao room moi luu vao config
                        ChannelVO newChannel = ChatManager.getInstance().addChannelManagerModel(channelUser, getParentExtension().getParentZone());
                        //Tao room thanh cong
                        if (newChannel != null) {
                            CreateRoomSettings cfgRoomChannel = new CreateRoomSettings();
                            cfgRoomChannel.setName(newChannel.id);
                            cfgRoomChannel.setGroupId(Params.Module.MODULE_CHAT);
                            cfgRoomChannel.setMaxUsers(newChannel.maxUser);
                            cfgRoomChannel.setGame(false);
                            cfgRoomChannel.setAutoRemoveMode(SFSRoomRemoveMode.NEVER_REMOVE);

                            List<RoomVariable> listVariableChannel = new ArrayList<>();
                            listVariableChannel.add(new SFSRoomVariable(Params.TYPE, EChatType.CHANNEL.getId()));
                            listVariableChannel.add(new SFSRoomVariable(Params.NAME, newChannel.name));
                            cfgRoomChannel.setRoomVariables(listVariableChannel);

                            cfgRoomChannel.setExtension(new CreateRoomSettings.RoomExtensionSettings(getParentExtension().getParentZone().getName(), ExtensionClass.CHAT_EXT));
                            try {
                                roomJoin = ExtensionUtility.getInstance().createRoom(getParentExtension().getParentZone(), cfgRoomChannel, null, true, null);
                                ExtensionUtility.getInstance().joinRoom(user, roomJoin);
                                break;
                            } catch (SFSCreateRoomException e) {
                                e.printStackTrace();
                                System.out.println();
                                System.out.println("----------------      Phòng chat channel đã tồn tại       --------------------");
                                System.out.println();
                            } catch (SFSJoinRoomException e) {
                                e.printStackTrace();
                                System.out.println();
                                System.out.println("----------------      Vào chat channel thất bại       --------------------");
                                System.out.println();
                            }
                        }
                    }

                    indexRoom++;
                }
            }
        }

        // Vao room chat guild
        GuildModel guildModel = GuildManager.getInstance().getGuildModelByUserID(uid, getParentExtension().getParentZone());
        if (guildModel != null) {
            roomJoin = getParentExtension().getParentZone().getRoomByName("guild".concat(ServerConstant.SEPARATER.concat(String.valueOf(guildModel.gId))));
            if (roomJoin != null) {
                while (true) {
                    try {
                        ExtensionUtility.getInstance().joinRoom(user, roomJoin);
                        break;
                    } catch (SFSJoinRoomException e) {
                        e.printStackTrace();
                        //Truong hop full room <- ko co vi khi tao room guild usermax = max user config guild cap cao nhat + 10
                        roomJoin.setMaxUsers(roomJoin.getMaxUsers() + 20);
                    }
                }
            } else {
                List<GuildVO> guildCf = GuildManager.getInstance().getGuildConfig();

                CreateRoomSettings cfgRoomChannel = new CreateRoomSettings();
                cfgRoomChannel.setName(GuildManager.getInstance().getNameRoomGuild(guildModel.gId));
                cfgRoomChannel.setGroupId(Params.Module.MODULE_CHAT);
                cfgRoomChannel.setMaxUsers(guildCf.get(guildCf.size() - 1).member + 10);
                cfgRoomChannel.setDynamic(true);
                cfgRoomChannel.setGame(false);
                cfgRoomChannel.setAutoRemoveMode(SFSRoomRemoveMode.WHEN_EMPTY);

                List<RoomVariable> listVariableChannel = new ArrayList<>();
                listVariableChannel.add(new SFSRoomVariable(Params.TYPE, EChatType.GUILD.getId()));
                listVariableChannel.add(new SFSRoomVariable(Params.NAME, String.valueOf(guildModel.gName)));
                cfgRoomChannel.setRoomVariables(listVariableChannel);

                cfgRoomChannel.setExtension(new CreateRoomSettings.RoomExtensionSettings(getParentExtension().getParentZone().getName(), ExtensionClass.CHAT_EXT));
                try {
                    roomJoin = ExtensionUtility.getInstance().createRoom(getParentExtension().getParentZone(), cfgRoomChannel, null, true, null);
                    ExtensionUtility.getInstance().joinRoom(user, roomJoin);
                } catch (SFSCreateRoomException e) {
                    e.printStackTrace();
                    System.out.println();
                    System.out.println("----------------      Phòng chat guild đã tồn tại       --------------------");
                    System.out.println();
                } catch (SFSJoinRoomException e) {
                    e.printStackTrace();
                    System.out.println();
                    System.out.println("----------------      Vào chat channel thất bại       --------------------");
                    System.out.println();
                }
            }
        }
    }
}
