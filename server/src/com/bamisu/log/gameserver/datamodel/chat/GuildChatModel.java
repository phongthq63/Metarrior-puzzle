package com.bamisu.log.gameserver.datamodel.chat;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.log.gameserver.datamodel.chat.entities.ChatModel;
import com.bamisu.log.gameserver.datamodel.chat.entities.IMessageChat;
import com.bamisu.log.gameserver.datamodel.chat.entities.InfoMessage;
import com.bamisu.log.gameserver.module.chat.ChatManager;
import com.bamisu.log.gameserver.module.chat.defind.EChatType;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.List;

public class GuildChatModel extends ChatModel {

    public long gid;
    public List<InfoMessage> message = new ArrayList<>();

    private final static short eachGet = ChatManager.getInstance().getChatConfig().see;
    private final static int limitMessage = EChatType.GUILD.getLimitMessageSave();
    private final static int limitTime = EChatType.GUILD.getLimitTimeSave();



    public static GuildChatModel create(long gid, Zone zone){
        GuildChatModel guildChatModel = new GuildChatModel();
        guildChatModel.gid = gid;
        guildChatModel.saveToDB(zone);

        return guildChatModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.gid), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static GuildChatModel copyFromDBtoObject(long gid, Zone zone) {
        return copyFromDBtoObject(String.valueOf(gid), zone);
    }

    public static GuildChatModel copyFromDBtoObject(String gid, Zone zone) {
        GuildChatModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(gid), GuildChatModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, GuildChatModel.class);
                if (pInfo != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        return pInfo;
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * doc tin nhan
     * @param location
     * @return
     */
    public List<InfoMessage> readMessage(int location, Zone zone){
        deleteMessage(zone);
        return readMessage(message, location, eachGet);
    }

    /**
     * luu tin nhan
     * @param uid
     * @param message
     * @param zone
     * @return
     */
    public boolean saveMessage(long uid, String message, Zone zone) {
        IMessageChat ms = new InfoMessage();
        addMessage(this.message, ms.create(uid, message));

        return saveToDB(zone);
    }

    /**
     * xoa tin nhan
     * @param zone
     * @return
     */
    private void deleteMessage(Zone zone) {
        long uid = deleteMessage(this.message, limitMessage, limitTime);

        //Kiem tra uid co ton tai trong map info user extension ko
        boolean flag = true;
        for(InfoMessage infoMessage : message){
            if(infoMessage.uid == uid){
                flag = false;
                break;
            }
        }
        Room room = zone.getRoomByName("guild".concat(ServerConstant.SEPARATER).concat(String.valueOf(gid)));
        if(flag && room != null){
            ISFSObject objPut = new SFSObject();
            objPut.putLong(Params.UID, uid);
            //Xoa thong qua internal message
            //Loi cast Chat extension to chat extension
            room.getExtension().handleInternalMessage(CMD.InternalMessage.REMOVE_INFO_USER_CHAT, objPut);
        }
    }
}
