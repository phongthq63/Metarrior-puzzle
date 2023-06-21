package com.bamisu.log.gameserver.datamodel.chat;

import com.bamisu.log.gameserver.datamodel.chat.entities.ChatModel;
import com.bamisu.log.gameserver.datamodel.chat.entities.IMessageChat;
import com.bamisu.log.gameserver.datamodel.chat.entities.InfoMessage;
import com.bamisu.log.gameserver.module.chat.ChatExtension;
import com.bamisu.log.gameserver.module.chat.ChatHandler;
import com.bamisu.log.gameserver.module.chat.ChatManager;
import com.bamisu.log.gameserver.module.chat.defind.EChatType;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.List;

public class ChannelChatModel extends ChatModel {
    public  String cid;
    public List<InfoMessage> message = new ArrayList<>();

    private static short eachGet = ChatManager.getInstance().getChatConfig().see;
    private static int limitMessage = EChatType.CHANNEL.getLimitMessageSave();
    private static int limitTime = EChatType.CHANNEL.getLimitTimeSave();



    public static ChannelChatModel create(String cid, Zone zone){
        ChannelChatModel channelChatModel = new ChannelChatModel();
        channelChatModel.cid = cid;
        channelChatModel.saveToDB(zone);

        return channelChatModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.cid), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ChannelChatModel copyFromDBtoObject(long cid, Zone zone) {
        return copyFromDBtoObject(String.valueOf(cid), zone);
    }

    public static ChannelChatModel copyFromDBtoObject(String id, Zone zone) {
        ChannelChatModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(id), ChannelChatModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, ChannelChatModel.class);
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
        if(flag){
            ISFSObject objPut = new SFSObject();
            objPut.putLong(Params.UID, uid);
            //Xoa thong qua internal message
            //Loi cast Chat extension to chat extension
            zone.getRoomByName(cid).getExtension().handleInternalMessage(CMD.InternalMessage.REMOVE_INFO_USER_CHAT, objPut);
        }
    }
}
