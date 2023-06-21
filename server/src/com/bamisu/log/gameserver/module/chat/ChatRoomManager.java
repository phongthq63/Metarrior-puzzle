package com.bamisu.log.gameserver.module.chat;

import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.chat.ChannelChatModel;
import com.bamisu.log.gameserver.datamodel.chat.GlobalChatModel;
import com.bamisu.log.gameserver.datamodel.chat.GuildChatModel;
import com.bamisu.log.gameserver.datamodel.chat.UserChatModel;
import com.bamisu.log.gameserver.datamodel.chat.entities.InfoMessage;
import com.bamisu.log.gameserver.datamodel.chat.entities.PackInfoMessage;
import com.bamisu.log.gameserver.datamodel.guild.GuildModel;
import com.bamisu.log.gameserver.datamodel.guild.UserGuildModel;
import com.bamisu.log.gameserver.datamodel.guild.entities.LogGuildInfo;
import com.bamisu.log.gameserver.module.chat.defind.EChatType;
import com.bamisu.log.gameserver.module.chat.entities.PackageInfoMessage;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatRoomManager {
    private ChatHandler handler;
    public ChatRoomManager(ChatHandler handler) {
        this.handler = handler;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public UserModel getUserChat(User user){
        return getUserChat(Long.parseLong(user.getName()));
    }
    public UserModel getUserChat(long uid){
        ISFSObject objPut = new SFSObject();
        objPut.putLong(Params.UID, uid);

        ISFSObject objGet = (ISFSObject) handler.getParentExtension().getParentZone().getExtension().handleInternalMessage(CMD.InternalMessage.GET_CACHE_USER_MODEL, objPut);
        return Utils.fromJson(objGet.getUtfString(Params.DATA), UserModel.class);
    }

    public UserGuildModel getUserGuildModel(long uid){
        ISFSObject objPut = new SFSObject();
        objPut.putLong(Params.UID, uid);

        ISFSObject objGet = (ISFSObject) handler.getParentExtension().getParentZone().getExtension().handleInternalMessage(CMD.InternalMessage.GET_CACHE_USER_GUILD_MODEL, objPut);
        return Utils.fromJson(objGet.getUtfString(Params.DATA), UserGuildModel.class);
    }

    public int getLevelUser(long uid){
        ISFSObject objPut = new SFSObject();
        objPut.putLong(Params.UID, uid);

        ISFSObject objGet = (ISFSObject) handler.getParentExtension().getParentZone().getExtension().handleInternalMessage(CMD.InternalMessage.GET_LEVEL_USER, objPut);
        return objGet.getInt(Params.DATA);
    }

    public List<LogGuildInfo> getLogGuild(long uid){
        List<List<LogGuildInfo>> list = new ArrayList<>();
        ISFSObject objPut = new SFSObject();
        objPut.putLong(Params.UID, uid);

        ISFSObject objGet = (ISFSObject) handler.getParentExtension().getParentZone().getExtension().handleInternalMessage(CMD.InternalMessage.GET_LOG_GUILD, objPut);
        list.addAll(Utils.fromJson(objGet.getUtfString(Params.DATA), list.getClass()));

        List<LogGuildInfo> logs = new ArrayList<>();
        for(List<LogGuildInfo> index : list){
            logs.addAll(index);
        }

        return logs;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    public boolean addUserToRoomChat(long uid, Zone zone){
        ISFSObject res = new SFSObject();
        res.putLong(Params.UID, uid);
        zone.getExtension().handleInternalMessage(CMD.InternalMessage.ADD_USER_ROOM_CHAT, res);
        return true;
    }
    public boolean removeUserFromChatRoom(long uid, Zone zone){
        ISFSObject res = new SFSObject();
        res.putLong(Params.UID, uid);
        zone.getExtension().handleInternalMessage(CMD.InternalMessage.REMOVE_USER_ROOM_CHAT, res);
        return true;
    }
    public boolean userInChat(long uid, Zone zone){
        ISFSObject res = new SFSObject();
        res.putLong(Params.UID, uid);
        ISFSObject rec = (ISFSObject) zone.getExtension().handleInternalMessage(CMD.InternalMessage.CHECK_USER_ROOM_CHAT, res);
        if(rec.containsKey(Params.DATA)){
            return rec.getBool(Params.DATA);
        }else {
            return false;
        }
    }
    public Set<Long> getListUserInChat(Zone zone){
        ISFSObject rec = (ISFSObject) zone.getExtension().handleInternalMessage(CMD.InternalMessage.LIST_USER_ROOM_CHAT, new SFSObject());
        if(rec.containsKey(Params.DATA)){
            return new HashSet<>(rec.getLongArray(Params.DATA));
        }else {
            return new HashSet<>();
        }
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Get module
     */
    public UserChatModel getUserChatModel(long uid, Zone zone){
        UserChatModel userChatModel = UserChatModel.copyFromDBtoObject(uid, zone);
        if(userChatModel == null){
            userChatModel = UserChatModel.create(uid, zone);
        }
        return userChatModel;
    }

    /**
     * Get module chat the gioi
     */
    public GlobalChatModel getGlobalChatModel(Zone zone){
        GlobalChatModel globalChatModel = GlobalChatModel.copyFromDBtoObject(zone);
        if(globalChatModel == null){
            globalChatModel = GlobalChatModel.create(zone);
        }
        return globalChatModel;
    }

    /**
     * Get module
     */
    public ChannelChatModel getChannelChatModel(String cid, Zone zone){
        ChannelChatModel channelChatModel = ChannelChatModel.copyFromDBtoObject(cid, zone);
        if(channelChatModel == null){
            channelChatModel = ChannelChatModel.create(cid, zone);
        }
        return channelChatModel;
    }

    /**
     * Get module
     */
    public GuildChatModel getGuildChatModel(long gid, Zone zone){
        GuildChatModel guildChatModel = GuildChatModel.copyFromDBtoObject(gid, zone);
        if(guildChatModel == null){
            guildChatModel = GuildChatModel.create(gid, zone);
        }
        return guildChatModel;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Dung de goi khi load scene
     * @param uid
     * @param zone
     * @return
     */
    public List<PackageInfoMessage> loadAllNewMessage(long uid, Zone zone){
        ISFSObject objPutInternal = new SFSObject();
        objPutInternal.putLong(Params.UID, uid);
        ISFSObject objGetInternal = (ISFSObject) zone.getExtension().handleInternalMessage(CMD.InternalMessage.GET_ALL_MESSAGE_CHAT, objPutInternal);
        List<PackageInfoMessage> listMessage = Utils.fromJsonList(objGetInternal.getUtfString(Params.DATA), PackageInfoMessage.class);

        return (listMessage != null) ? listMessage : new ArrayList<>();
    }

    /**
     * Kiem tra gioi han de gui di tin nhan
     */
    public boolean canSendMessage(long uid, EChatType type, Zone zone){
        return getUserChatModel(uid, zone).canSendMessage(type, zone);
    }

    /**
     * Lay cac tin nhan trong chat the gioi
     * @param location
     * @param zone
     * @return
     */
    public List<InfoMessage> getListMessageGlobalChat(int location, Zone zone){
        return getGlobalChatModel(zone).readMessage(location, zone);
    }

    /**
     * Lay cac tin nhan
     * @param location
     * @param zone
     * @return
     */
    public List<InfoMessage> getListMessageChannelChat(String cid, int location, Zone zone){
        return getChannelChatModel(cid, zone).readMessage(location, zone);
    }

    /**
     * Lay cac tin nhan
     * @param location
     * @param zone
     * @return
     */
    public List<InfoMessage> getListMessageGuildChat(long gid, int location, Zone zone){
        return getGuildChatModel(gid, zone).readMessage(location, zone);
    }

    /**
     * Lay cac tin nhan
     * @param location
     * @param zone
     * @return
     */
    public List<InfoMessage> getListMessagePrivateChat(long uid ,long target, int location, Zone zone){
        return getUserChatModel(uid, zone).readMessagePrivate(target, location, zone);
    }
    public List<PackInfoMessage> getListMessagePrivateChat(long uid, Zone zone){
        return getUserChatModel(uid, zone).readMessagePrivate(zone);
    }

    /**
     * Gui tin nhan
     * @param uid
     * @param massage
     * @param zone
     * @return
     */
    public boolean saveMessageGlobalChat(long uid, String massage, Zone zone){
        return getGlobalChatModel(zone).saveMessage(uid, massage, zone);
    }

    /**
     * Gui tin nhan
     * @param uid
     * @param massage
     * @param zone
     * @return
     */
    public boolean saveMessageChannelChat(String cid, long uid, String massage, Zone zone){
        if(cid == null || cid.isEmpty()) return false;
        return getChannelChatModel(cid, zone).saveMessage(uid, massage, zone);
    }

    public String getUserChannelChat(long uid, Zone zone){
        return getUserChatModel(uid, zone).readChannel();
    }

    /**
     * Gui tin nhan
     * @param uid
     * @param massage
     * @param zone
     * @return
     */
    public boolean saveMessageGuildChat(long gid, long uid, String massage, Zone zone){
        return getGuildChatModel(gid, zone).saveMessage(uid, massage, zone);
    }

    /**
     * Gui tin nhan
     * @param sender nguoi gui
     * @param massage nguoi nhan
     * @param zone
     * @return
     */
    public boolean saveMessagePrivateChat(long sender, long receiver, String massage, Zone zone){
        return getUserChatModel(receiver, zone).saveMessagePrivate(sender, sender, massage, zone) &&
                getUserChatModel(sender, zone).saveMessagePrivate(sender, receiver, massage, zone);
    }
}
