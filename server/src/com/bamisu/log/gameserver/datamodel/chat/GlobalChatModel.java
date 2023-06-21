package com.bamisu.log.gameserver.datamodel.chat;

import com.bamisu.log.gameserver.datamodel.chat.entities.ChatModel;
import com.bamisu.log.gameserver.datamodel.chat.entities.IMessageChat;
import com.bamisu.log.gameserver.datamodel.chat.entities.InfoMessage;
import com.bamisu.log.gameserver.module.chat.ChatExtension;
import com.bamisu.log.gameserver.module.chat.ChatHandler;
import com.bamisu.log.gameserver.module.chat.ChatManager;
import com.bamisu.log.gameserver.module.chat.defind.EChatType;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.ArrayList;
import java.util.List;

public class GlobalChatModel extends ChatModel {

    public static final String id = "global";
    public List<InfoMessage> message = new ArrayList<>();

    private final static short eachGet = ChatManager.getInstance().getChatConfig().see;
    private final static int limitMessage = EChatType.GLOBAL.getLimitMessageSave();
    private final static int limitTime = EChatType.GLOBAL.getLimitTimeSave();



    public static GlobalChatModel create(Zone zone){
        GlobalChatModel localChatModel = new GlobalChatModel();
        localChatModel.saveToDB(zone);

        return localChatModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(this.id, zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static GlobalChatModel copyFromDBtoObject(Zone zone) {
        GlobalChatModel pInfo = null;
        try {
            String str = (String) getModel(id, GlobalChatModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, GlobalChatModel.class);
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
            zone.getRoomByName(id).getExtension().handleInternalMessage(CMD.InternalMessage.REMOVE_INFO_USER_CHAT, objPut);
        }
    }
}
