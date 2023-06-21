package com.bamisu.log.gameserver.module.chat;

import com.bamisu.log.gameserver.datamodel.chat.*;
import com.bamisu.log.gameserver.datamodel.chat.entities.InfoMessage;
import com.bamisu.log.gameserver.datamodel.chat.entities.PackInfoMessage;
import com.bamisu.log.gameserver.datamodel.guild.UserGuildModel;
import com.bamisu.log.gameserver.module.chat.defind.EChatType;
import com.bamisu.log.gameserver.module.chat.entities.PackageInfoMessage;
import com.bamisu.log.gameserver.module.chat.config.ChannelConfig;
import com.bamisu.log.gameserver.module.chat.config.ChatConfig;
import com.bamisu.log.gameserver.module.chat.config.entities.ChannelVO;
import com.bamisu.log.gameserver.module.chat.config.entities.ChatVO;
import com.bamisu.log.gameserver.module.guild.GuildManager;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.*;

public class ChatManager {
    private static ChatManager ourInstance = new ChatManager();

    public static ChatManager getInstance() {
        return ourInstance;
    }

    private ChatManager() {
        loadConfig();
    }

    private ChannelConfig channelConfig;
    private ChatConfig chatConfig;



    private void loadConfig(){
        channelConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Chat.FILE_PATH_CONFIG_CHANNEL), ChannelConfig.class);
        chatConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Chat.FILE_PATH_CONFIG_CHAT), ChatConfig.class);
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    public boolean removeUserFromChatRoom(long uid, Zone zone){
        ISFSObject res = new SFSObject();
        res.putLong(Params.UID, uid);
        zone.getExtension().handleInternalMessage(CMD.InternalMessage.REMOVE_USER_ROOM_CHAT, res);
        return true;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Dung de goi khi load scene
     * @param uid
     * @param zone
     * @return
     */
    public List<PackageInfoMessage> loadAllNewMessage(long uid, Zone zone){
        List<PackageInfoMessage> list = new ArrayList<>();
        //tin nhan local
        list.add(new PackageInfoMessage(EChatType.GLOBAL, PackInfoMessage.create(0, ChatManager.getInstance().getListMessageGlobalChat(0, zone))));
        //tin nhan kenh
        UserChatModel userChatModel = getUserChatModel(uid, zone);
        if(userChatModel.readChannel() != null && !userChatModel.readChannel().isEmpty()){
            list.add(new PackageInfoMessage(EChatType.CHANNEL, PackInfoMessage.create(0, getListMessageChannelChat(userChatModel.readChannel(), 0, zone))));
        }
        //tin nhan guild
        ISFSObject objPut = new SFSObject();
        objPut.putLong(Params.UID, uid);

        UserGuildModel userGuildModel = GuildManager.getInstance().getUserGuildModel(uid, zone);
        if(userGuildModel.inGuild()){
            list.add(new PackageInfoMessage(EChatType.GUILD, PackInfoMessage.create(0, getListMessageGuildChat(userGuildModel.gid, 0, zone))));
        }
        //tin nhan rieng
        List<PackInfoMessage> listPrivate = getListMessagePrivateChat(uid, zone);
        for(PackInfoMessage pri : listPrivate){
            list.add(new PackageInfoMessage(EChatType.PRIVATE, pri));
        }

        return list;
    }

    /**
     * Kiem tra gioi han de gui di tin nhan
     */
    public boolean canSendMessage(long uid, EChatType type, Zone zone){
        return getUserChatModel(uid, zone).canSendMessage(type, zone);
    }

    /**
     * chuyen kenh chat
     */
    public boolean changeChannelChat(long uid, String idChannel, Zone zone){
        return getUserChatModel(uid, zone).changeChannel(idChannel, zone);
    }



    /*------------------------------------------------------ PRIVATE -------------------------------------------------*/
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
     * @param sender nguoi gui
     * @param massage nguoi nhan
     * @param zone
     * @return
     */
    public boolean saveMessagePrivateChat(long sender, long receiver, String massage, Zone zone){
        return getUserChatModel(receiver, zone).saveMessagePrivate(sender, sender, massage, zone) &&
                getUserChatModel(sender, zone).saveMessagePrivate(sender, receiver, massage, zone);
    }

    public boolean deleteMessagePrivateChat(long uid, long deleted, Zone zone){
        UserChatModel userChatModel = getUserChatModel(uid, zone);
        if(userChatModel.removeMessagePrivateUser(deleted, zone)){
            return true;
        }else {
            return true;
        }
    }



    /*------------------------------------------------------ GLOBAL --------------------------------------------------*/
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
     * Lay cac tin nhan trong chat the gioi
     * @param location
     * @param zone
     * @return
     */
    public List<InfoMessage> getListMessageGlobalChat(int location, Zone zone){
        return getGlobalChatModel(zone).readMessage(location, zone);
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



    /*------------------------------------------------------ CHANNEL -------------------------------------------------*/
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
     * Lay cac tin nhan
     * @param location
     * @param zone
     * @return
     */
    public List<InfoMessage> getListMessageChannelChat(String cid, int location, Zone zone){
        return getChannelChatModel(cid, zone).readMessage(location, zone);
    }



    /*------------------------------------------------------- GUILD --------------------------------------------------*/
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
     * Gui tin nhan
     * @param uid
     * @param massage
     * @param zone
     * @return
     */
    public boolean saveMessageGuildChat(long gid, long uid, String massage, Zone zone){
        return getGuildChatModel(gid, zone).saveMessage(uid, massage, zone);
    }




    /*------------------------------------------------------ CONFIG - ------------------------------------------------*/
    public ChannelManagerModel getChannelManagerModel(Zone zone){
        return ChannelManagerModel.copyFromDBtoObject(zone);
    }

    public ChannelVO addChannelManagerModel(ChannelVO channelVO, Zone zone){
        ChannelManagerModel channelManagerModel = getChannelManagerModel(zone);

        ChannelVO newChannel = channelConfig.createChannel(channelVO, channelManagerModel.readChannel(zone));
        return channelManagerModel.addChannel(newChannel, zone) ? newChannel : null;
    }

    public ChannelVO getListChannel(String id, Zone zone){
        for(ChannelVO index : getListChannel(zone)){
            if(index.id.equals(id)){
                return index;
            }
        }
        return null;
    }
    public List<ChannelVO> getListChannel(Zone zone){
        ChannelManagerModel channelManagerModel = getChannelManagerModel(zone);
        return channelManagerModel.readChannel(zone);
    }


    /*------------------------------------------------------ CONFIG - ------------------------------------------------*/
    /**
     * Get channel config
     * @return
     */
    public List<ChannelVO> getChannelConfig(){
        return channelConfig.list;
    }
    public ChannelVO getChannelConfig(String id){
        for(ChannelVO index : getChannelConfig()){
            if(index.id.equals(id)){
                return index;
            }
        }
        return null;
    }

    public List<ChannelVO> getChannelConfigDependType(String type){
        List<ChannelVO> listChannel = new ArrayList<>();
        for(ChannelVO index : getChannelConfig()){
            if(index.type.equals(type)){
                listChannel.add(index);
            }
        }
        return listChannel;
    }


    /**
     * Get chat config
     */
    public ChatConfig getChatConfig(){
        return chatConfig;
    }
    public ChatVO getChatConfig(int id){
        for(ChatVO index : getChatConfig().list){
            if(index.id == id){
                return index;
            }
        }
        return null;
    }
    public ChatVO getChatConfigDependType(String type){
        for(ChatVO index : getChatConfig().list){
            if(index.description.toLowerCase().equals(type.toLowerCase())){
                return index;
            }
        }
        return null;
    }
}
