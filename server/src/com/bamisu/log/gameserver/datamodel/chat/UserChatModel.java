package com.bamisu.log.gameserver.datamodel.chat;

import com.bamisu.log.gameserver.datamodel.chat.entities.*;
import com.bamisu.log.gameserver.module.chat.ChatManager;
import com.bamisu.log.gameserver.module.chat.defind.EChatType;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.*;

public class UserChatModel extends ChatModel {

    public long uid;
    public String channelChat = "";
    public List<PackInfoMessage> chatPrivate = new ArrayList<>();
    public Map<Integer,Integer> timeStamp = new HashMap<>();

    private final static short eachGet = ChatManager.getInstance().getChatConfig().see;
    private final static int limitMessage = EChatType.PRIVATE.getLimitMessageSave();
    private final static int limitTime = EChatType.PRIVATE.getLimitTimeSave();
    private final static short limitUser = ChatManager.getInstance().getChatConfig().limitPrivate;



    public static UserChatModel create(long uid, Zone zone){
        UserChatModel userChatModel = new UserChatModel();
        userChatModel.uid = uid;
        userChatModel.saveToDB(zone);

        return userChatModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uid), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static UserChatModel copyFromDBtoObject(long uid, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uid), zone);
    }

    public static UserChatModel copyFromDBtoObject(String uid, Zone zone) {
        UserChatModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(uid), UserChatModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, UserChatModel.class);
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
    public String readChannel(){
        return channelChat;
    }
    /**
     * CHuyen channel
     */
    public boolean changeChannel(String idChannel, Zone zone){
        channelChat = idChannel;
        return saveToDB(zone);
    }

    /**
     * doc tin nhan
     * @return
     */
    public List<PackInfoMessage> readMessagePrivate(Zone zone){
        deleteMessagePrivate(zone);
        List<PackInfoMessage> packMs = new ArrayList<>();
        for(PackInfoMessage message : chatPrivate){
            packMs.add(PackInfoMessage.create(message.uid, readMessage(message.chat, 0, eachGet)));
        }
        return packMs;
    }
    public List<InfoMessage> readMessagePrivate(long uid, int location, Zone zone){
        deleteMessagePrivate(zone);
        for(PackInfoMessage message : chatPrivate){
            if(message.uid == uid){
                return readMessage(message.chat, location, eachGet);
            }
        }
        return new ArrayList<>();
    }

    /**
     * luu tin nhan
     * @param sender
     * @param message
     * @param zone
     * @return
     */
    public boolean saveMessagePrivate(long sender, long receiver, String message, Zone zone) {
        boolean flag = false;
        IMessageChat ms = new InfoMessage();
        for(PackInfoMessage list : chatPrivate){
            //Them vao list cua nguoi choi neu tim thay
            if(list.uid == receiver){
                addMessage(list.chat, ms.create(sender, message));
                flag = true;
                break;
            }
        }
        //Khong tim thay
        if(!flag){
            List<InfoMessage> list = new ArrayList<>();
            addMessage(list, ms.create(sender, message));
            chatPrivate.add(0, PackInfoMessage.create(receiver, list));
        }

        return saveToDB(zone);
    }

    /**
     * xoa tin nhan
     * @param zone
     * @return
     */
    private void deleteMessagePrivate(Zone zone) {
        //Ktra gioi han luu truu nguoi choi
        for(int i = chatPrivate.size() - 1; i >= 0; i--){
            if(chatPrivate.size() <= limitUser)break;
            chatPrivate.remove(i);
        }
        //Kiem tra gioi han thoi gian + so luong tin nhan
        for(PackInfoMessage pack : chatPrivate){
            deleteMessage(pack.chat, limitMessage, limitTime);
        }
        saveToDB(zone);
    }
    public boolean removeMessagePrivateUser(long uid, Zone zone){
        Iterator<PackInfoMessage> iterator = chatPrivate.iterator();
        PackInfoMessage entry;

        while (iterator.hasNext()){
            entry = iterator.next();

            if(entry.uid == uid){
                iterator.remove();
                return saveToDB(zone);
            }
        }
        return false;
    }

    /**
     * Kiem tra xem co the gui tin nhan khong
     */
    public final boolean canSendMessage(EChatType type, Zone zone){
        //Neu ko gui dc
        //Thoi gian ko dap ung yeu cau
        int now = Utils.getTimestampInSecond();
        if(timeStamp.containsKey(type.getId()) && (now - timeStamp.get(type.getId()) <= type.getLimitTimeSend())){
            return false;
        }
        //Neu gui dc
        //Luu time moi
        timeStamp.put(type.getId(), now);
        return saveToDB(zone);
    }
}
