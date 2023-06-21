package com.bamisu.log.gameserver.datamodel.chat;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.chat.ChatManager;
import com.bamisu.log.gameserver.module.chat.config.entities.ChannelVO;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class ChannelManagerModel extends DataModel {
    private final static long id = 0;

    public List<ChannelVO> listChannelChat = new ArrayList<>();
    private final Object lockChannel = new Object();



    public static ChannelManagerModel createChannelManagerModel(Zone zone){
        ChannelManagerModel channelManagerModel = new ChannelManagerModel();
        channelManagerModel.saveToDB(zone);

        return channelManagerModel;
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.id), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ChannelManagerModel copyFromDBtoObject(Zone zone) {
        ChannelManagerModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(id), ChannelManagerModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, ChannelManagerModel.class);
                if (pInfo != null) {
//                    pInfo.wrNiteLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        if(pInfo == null){
            pInfo = createChannelManagerModel(zone);
        }
        return pInfo;
    }



    /*-----------------------------------------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------------------------------------*/
    public boolean addChannel(ChannelVO channel, Zone zone){
        synchronized (lockChannel){
            listChannelChat.add(channel);
            return saveToDB(zone);
        }
    }

    public List<ChannelVO> readChannel(Zone zone){
        synchronized (lockChannel){
            if(listChannelChat.isEmpty()){
                listChannelChat.addAll(ChatManager.getInstance().getChannelConfig());
                saveToDB(zone);
            }

            return listChannelChat;
        }
    }
}
