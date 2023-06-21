package com.bamisu.log.gameserver.module.chat;

import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.chat.UserChatModel;
import com.bamisu.log.gameserver.datamodel.chat.entities.InfoMessage;
import com.bamisu.log.gameserver.datamodel.guild.GuildModel;
import com.bamisu.log.gameserver.datamodel.guild.UserGuildModel;
import com.bamisu.log.gameserver.module.GameEvent.GameEventAPI;
import com.bamisu.log.gameserver.module.GameEvent.defind.EGameEvent;
import com.bamisu.log.gameserver.module.chat.cmd.rec.RecGetMessage;
import com.bamisu.log.gameserver.module.chat.cmd.rec.RecSendMessage;
import com.bamisu.log.gameserver.module.chat.cmd.send.*;
import com.bamisu.log.gameserver.module.chat.defind.EChatType;
import com.bamisu.log.gameserver.module.chat.entities.PackageInfoMessage;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.module.notification.defind.ENotification;
import com.bamisu.log.gameserver.module.pushnotify.PushNotifyManager;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.ISFSEventParam;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.*;
import java.util.stream.Collectors;


public class ChatHandler extends ExtensionBaseClientRequestHandler {

    private ChatRoomManager manager;

    public ChatHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_CHAT;
        this.manager = new ChatRoomManager(this);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId) {
            case CMD.CMD_LOAD_SCENE_CHAT:
                doLoadSceneChat(user, data);
                break;
            case CMD.CMD_LOAD_SCENE_CHAT_GUILD:
                doLoadSceneChatGuild(user, data);
                break;
            case CMD.CMD_LEAVE_SCENE_CHAT:
                doLeaveSceneChat(user, data);
                break;
            case CMD.CMD_SEND_MESSAGE_TEXT:
                doSendMessageText(user, data);
                break;
            case CMD.CMD_GET_MESSAGE:
                doGetMessage(user, data);
                break;
            case CMD.CMD_REMOVE_ALL_MESSAGE_USER:
                doRemoveAllMessageUser(user, data);
                break;
        }
    }

    @WithSpan
    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {
        switch (type) {
            case USER_LEAVE_ROOM:
                onUserLeaveRoom(event);
                break;
        }
    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_CHAT, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_CHAT, this);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * Lay cac tin nhan moi nhat
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doLoadSceneChat(User user, ISFSObject data) {
        long uid = manager.getUserChat(user).userID;

        //List tin nhan
        SendLoadSceneChat objPut = new SendLoadSceneChat();
        objPut.manager = manager;
        objPut.pack = manager.loadAllNewMessage(uid, getParentExtension().getParentZone());
        objPut.zone = getParentExtension().getParentZone();
        send(objPut, user);

        //Add
        manager.addUserToRoomChat(uid, getParentExtension().getParentZone());
    }

    /**
     * Lay cac tin nhan moi nhat
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doLoadSceneChatGuild(User user, ISFSObject data) {
        long uid = manager.getUserChat(user).userID;

        //List tin nhan
        SendLoadSceneChatGuild objPut = new SendLoadSceneChatGuild();
        objPut.manager = manager;
        objPut.pack = manager.loadAllNewMessage(uid, getParentExtension().getParentZone());
        objPut.log = manager.getLogGuild(uid);
        objPut.zone = getParentExtension().getParentZone();
        send(objPut, user);

        //Add
        manager.addUserToRoomChat(uid, getParentExtension().getParentZone());
    }

    /**
     * Roi khoi man hinh chat -> xac dinh gui notify hay message
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doLeaveSceneChat(User user, ISFSObject data) {
        long uid = manager.getUserChat(user).userID;

        if (manager.removeUserFromChatRoom(uid, getParentExtension().getParentZone())) {
            SendLeaveSceneChat objPut = new SendLeaveSceneChat();
            send(objPut, user);
            return;
        }

        SendLeaveSceneChat objPut = new SendLeaveSceneChat();
        send(objPut, user);
    }


    /**
     * Gui tin nhan text
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doSendMessageText(User user, ISFSObject data) {
        long uid = manager.getUserChat(user).userID;
        RecSendMessage objGet = new RecSendMessage(data);

        if (objGet.to == uid) {
            SendSendMessage objPut = new SendSendMessage(ServerConstant.ErrorCode.ERR_CHAT_TO_YOURSEFT);
            send(objPut, user);
            return;
        }

        if (!manager.canSendMessage(uid, EChatType.fromID(objGet.type), getParentExtension().getParentZone())) {
            SendSendMessage objPut = new SendSendMessage(ServerConstant.ErrorCode.ERR_CHAT_TOO_FAST);
            send(objPut, user);
            return;
        }

        //Notify message
        handlerNotifyMessage(user, objGet);

        //Gui tin nhan di
        handlerSendMessage(user, objGet);

        //Save message
        handlerSaveMessage(user, objGet);
    }


    /**
     * Lay cac tin nhan khi scoll
     *
     * @param user
     * @param data
     */
    @WithSpan
    private void doGetMessage(User user, ISFSObject data) {
        long uid = manager.getUserChat(user).userID;
        RecGetMessage objGet = new RecGetMessage(data);
        List<InfoMessage> dataMessage = new ArrayList<>();
        switch (EChatType.fromID(objGet.type)) {
            case GLOBAL:
                dataMessage = manager.getListMessageGlobalChat(objGet.position, getParentExtension().getParentZone());
                break;
            case CHANNEL:
                UserChatModel userChatModel = manager.getUserChatModel(uid, getParentExtension().getParentZone());
                if (userChatModel.readChannel() == null || userChatModel.readChannel().isEmpty()) break;

                dataMessage = manager.getListMessageChannelChat(userChatModel.readChannel(), objGet.position, getParentExtension().getParentZone());
                break;
            case GUILD:
                UserGuildModel userGuildModel = manager.getUserGuildModel(uid);
                if (userGuildModel.inGuild()) {
                    dataMessage = manager.getListMessageGuildChat(userGuildModel.gid, objGet.position, getParentExtension().getParentZone());
                }
                break;
            case PRIVATE:
                dataMessage = manager.getListMessagePrivateChat(uid, objGet.uid, objGet.position, getParentExtension().getParentZone());
                if (dataMessage.size() <= 0) {
                    dataMessage = manager.getListMessagePrivateChat(objGet.uid, uid, objGet.position, getParentExtension().getParentZone());
                }
                break;
        }

        SendGetMessage objPut = new SendGetMessage();
        objPut.manager = manager;
        objPut.userManager = extension.getUserManager();
        objPut.listMessage = dataMessage;
        objPut.zone = getParentExtension().getParentZone();
        send(objPut, user);
    }

    /**
     *
     * @param user
     * @param data
     */
    private void doRemoveAllMessageUser(User user, ISFSObject data){














    }



    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * Send message
     *
     * @param user
     * @param objGet
     */
    @WithSpan
    private void handlerSendMessage(User user, RecSendMessage objGet) {
        UserModel userModel = manager.getUserChat(user);

        //Message
        SendSendMessage objPut = new SendSendMessage();
        objPut.userModel = userModel;
        objPut.receider = objGet.to;
        objPut.message = objGet.message;
        objPut.type = objGet.type;
        objPut.manager = manager;

        User reveicer = null;
        switch (EChatType.fromID(objGet.type)) {
            case GLOBAL:
            {
                send(objPut,
                        manager.getListUserInChat(getParentExtension().getParentZone()).stream().
                                map(id -> ExtensionUtility.getInstance().getUserById(id)).
                                filter(Objects::nonNull).
                                collect(Collectors.toList()));

                ISFSObject data = new SFSObject();
                data.putLong(Params.FROM, userModel.userID);
                getParentExtension().getParentZone().getExtension().handleInternalMessage(CMD.InternalMessage.ON_GLOBAL_CHAT, data);
                break;
            }
            case CHANNEL:
            {
                String nameRoom = manager.getUserChannelChat(userModel.userID, getParentExtension().getParentZone());
                Room room = extension.getParentZone().getRoomByName(nameRoom);
                List<User> listUser = room.getUserList();

                send(objPut,
                        manager.getListUserInChat(getParentExtension().getParentZone()).stream().
                                map(id -> ExtensionUtility.getInstance().getUserById(id)).
                                filter(userSFS -> userSFS != null && listUser.contains(userSFS)).
                                collect(Collectors.toList()));

                ISFSObject data = new SFSObject();
                data.putLong(Params.FROM, userModel.userID);
                data.putUtfString(Params.CHANEL_ID, nameRoom);
                getParentExtension().getParentZone().getExtension().handleInternalMessage(CMD.InternalMessage.ON_CHANEL_CHAT, data);
                break;
            }
            case GUILD:
            {
                UserGuildModel userGuildModel = manager.getUserGuildModel(userModel.userID);
                if(!userGuildModel.inGuild()) break;
                GuildModel guildModel = GuildModel.copyFromDBtoObject(userGuildModel.gid, getParentExtension().getParentZone());

                send(objPut,
                        manager.getListUserInChat(getParentExtension().getParentZone()).stream().
                                filter(uid -> guildModel.member.contains(uid)).
                                map(uid -> ExtensionUtility.getInstance().getUserById(uid)).
                                filter(Objects::nonNull).
                                collect(Collectors.toList()));

                ISFSObject data = new SFSObject();
                data.putLongArray(Params.TO, guildModel.member);
                getParentExtension().getParentZone().getExtension().handleInternalMessage(CMD.InternalMessage.ON_ALLIANCE_CHAT, data);
                break;
            }
            case PRIVATE:
                reveicer = ExtensionUtility.getInstance().getUserById(objGet.to);
                if (reveicer != null) {
                    //nguoi nhan offline
                    send(objPut, reveicer);
                } else {
                    //push notify
                    ISFSObject data = new SFSObject();
                    data.putLong(Params.FROM, userModel.userID);
                    data.putLong(Params.TO, objGet.to);
                    getParentExtension().getParentZone().getExtension().handleInternalMessage(CMD.InternalMessage.ON_PRIVATE_CHAT, data);
                }
                send(objPut, user);
                break;
        }

        //Event
        ISFSObject dataInteral = new SFSObject();
        dataInteral.putLong(Params.UID, userModel.userID);
        dataInteral.putUtfString(Params.EVENT, EGameEvent.CHAT.getId());
        //Data event
        Map<String, Object> data = new HashMap<>();
        data.put(Params.TYPE, objGet.type);
        dataInteral.putUtfString(Params.DATA, Utils.toJson(data));
        extension.getParentZone().getExtension().handleInternalMessage(CMD.InternalMessage.ARISE_GAME_EVENT, dataInteral);
    }

    /**
     * Notify Message
     *
     * @param objGet
     */
    @WithSpan
    private void handlerNotifyMessage(User sender, RecSendMessage objGet) {
        User reveicer = ExtensionUtility.getInstance().getUserById(objGet.to);
        long uidSend = extension.getUserManager().getUserModel(sender).userID;
        int type = objGet.type;

        List<String> params = new ArrayList<>();
        ISFSObject objPut = new SFSObject();

        switch (EChatType.fromID(type)) {
            case GLOBAL: {
                params.add(String.valueOf(type));
                String idNoti = ENotification.HAVE_MESSAGE_CHAT.getNotifyID(params);
                if (idNoti == null) break;

                objPut.putLongArray(Params.UID, manager.getListUserInChat(getParentExtension().getParentZone()));
                objPut.putUtfString(Params.ID, idNoti);

                getParentExtension().getParentZone().getExtension().handleInternalMessage(CMD.InternalMessage.SEND_NOTIFY, objPut);
                break;
            }
            case CHANNEL: {
                params.add(String.valueOf(type));
                String idNoti = ENotification.HAVE_MESSAGE_CHAT.getNotifyID(params);
                if (idNoti == null) break;

                String nameRoom = manager.getUserChannelChat(uidSend, getParentExtension().getParentZone());
                Room room = extension.getParentZone().getRoomByName(nameRoom);
                List<Long> listUserID = room.getUserList().stream().
                        map(user -> extension.getUserManager().getUserModel(user).userID).collect(Collectors.toList());

                objPut.putLongArray(Params.UID, manager.getListUserInChat(getParentExtension().getParentZone()).stream().
                        filter(listUserID::contains).collect(Collectors.toList()));
                objPut.putUtfString(Params.ID, idNoti);

                getParentExtension().getParentZone().getExtension().handleInternalMessage(CMD.InternalMessage.SEND_NOTIFY, objPut);
                break;
            }
            case GUILD:
            {
                params.add(String.valueOf(type));
                String idNoti = ENotification.HAVE_MESSAGE_CHAT.getNotifyID(params);
                if (idNoti == null) break;

                UserGuildModel userGuildModel = manager.getUserGuildModel(uidSend);
                if(!userGuildModel.inGuild()) break;
                GuildModel guildModel = GuildModel.copyFromDBtoObject(userGuildModel.gid, getParentExtension().getParentZone());
                if(guildModel == null) break;

                objPut.putLongArray(Params.UID, manager.getListUserInChat(getParentExtension().getParentZone()).stream().
                        filter(guildModel.member::contains).collect(Collectors.toList()));
                objPut.putUtfString(Params.ID, idNoti);

                getParentExtension().getParentZone().getExtension().handleInternalMessage(CMD.InternalMessage.SEND_NOTIFY, objPut);
                break;
            }
            case PRIVATE: {
                if (reveicer == null || manager.userInChat(objGet.to, getParentExtension().getParentZone())) break;
                long uidRec = extension.getUserManager().getUserModel(reveicer).userID;

                params.add(String.valueOf(type));
                params.add(String.valueOf(uidSend));
                String idNoti = ENotification.HAVE_MESSAGE_CHAT.getNotifyID(params);
                if (idNoti == null) break;

                objPut.putLongArray(Params.UID, Collections.singleton(uidRec));
                objPut.putUtfString(Params.ID, idNoti);

                getParentExtension().getParentZone().getExtension().handleInternalMessage(CMD.InternalMessage.SEND_NOTIFY, objPut);
                break;
            }
        }
    }

    /**
     * Save to db
     *
     * @param user
     * @param objGet
     */
    @WithSpan
    private void handlerSaveMessage(User user, RecSendMessage objGet) {
        UserModel sender = manager.getUserChat(user);
        switch (EChatType.fromID(objGet.type)) {
            case GLOBAL:
                manager.saveMessageGlobalChat(sender.userID, objGet.message, getParentExtension().getParentZone());
                break;
            case CHANNEL:
                String idChannel = manager.getUserChannelChat(sender.userID, getParentExtension().getParentZone());
                manager.saveMessageChannelChat(idChannel, sender.userID, objGet.message, getParentExtension().getParentZone());
                break;
            case GUILD:
                UserGuildModel userGuildModel = manager.getUserGuildModel(sender.userID);
                manager.saveMessageGuildChat(userGuildModel.gid, sender.userID, objGet.message, getParentExtension().getParentZone());
                break;
            case PRIVATE:
                UserModel receiver = extension.getUserManager().getUserModel(objGet.to);
                manager.saveMessagePrivateChat(sender.userID, receiver.userID, objGet.message, getParentExtension().getParentZone());
                break;
        }
    }


    /*----------------------------------------------------------------------------------------------------------------*/
    @WithSpan
    private void onUserLeaveRoom(ISFSEvent event) {
        User user = (User) event.getParameter(SFSEventParam.USER);
        UserModel userModel = extension.getUserManager().getUserModel(user);
        UserGuildModel userGuildModel = manager.getUserGuildModel(userModel.userID);

        if(!userGuildModel.inGuild()) return;
        Room room = getParentExtension().getParentZone().getRoomByName(GuildManager.getInstance().getNameRoomGuild(userGuildModel.gid));
        if(room == null) return;
        if(room.getUserList().size() <= 1){
            ExtensionUtility.getInstance().removeRoom(room);
        }
    }



    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * xoa thong tin user chat
     *
     * @param rec
     * @return
     */
    @WithSpan
    public ISFSObject RemoveInfoUserChat(ISFSObject rec) {
        long uid = rec.getLong(Params.UID);
        //Xoa info user chat
        manager.removeUserFromChatRoom(uid, getParentExtension().getParentZone());

        return new SFSObject();
    }

    /**
     * xoa thong tin user chat
     * @param rec
     * @return
     */
    @WithSpan
    public void SendLogGuildChat(ISFSObject rec){
        long gid = rec.getLong(Params.ID);
        String id = rec.getUtfString(Params.LOGS);
        List<String> params = new ArrayList<>(rec.getUtfStringArray(Params.PARAM));

        Room room = getParentExtension().getParentZone().getRoomByName(GuildManager.getInstance().getNameRoomGuild(gid));
        if(room == null) return;

        SendSendLog objPut = new SendSendLog();
        objPut.id = id;
        objPut.params = params;
        Set<Long> userInChat = manager.getListUserInChat(getParentExtension().getParentZone());
        send(objPut, room.getUserList().stream().
                filter(user -> userInChat.contains(manager.getUserChat(user).userID)).
                collect(Collectors.toList()));
    }
}
