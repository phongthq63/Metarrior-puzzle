package com.bamisu.log.gameserver.datamodel.chat.entities;

import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class ChatModel extends DataModel {

    public List<InfoMessage> readMessage(List<InfoMessage> message, int location, int count){
        List<InfoMessage> get = new ArrayList<>();
        for(int i = location; i < message.size(); i++){
            if(count <= 0)break;
            get.add(message.get(i));
            count--;
        }
        return get;
    }

    public void addMessage(List<InfoMessage> list, InfoMessage message){
        list.add(0, message);
    }

    public long deleteMessage(List<InfoMessage> list, int limitCount, int limitTime){
        //Duyet toan bi list
        boolean flag = true;
        for(int i = list.size() - 1; i >= 0; i--){
//            if(list.size() > limitCount && (Utils.getTimestampInSecond() - list.get(i).time) > limitTime){
            if(list.size() > limitCount){
                long uid = list.get(i).uid;
                list.remove(i);
                return uid;
            }
            break;
        }
        return -1;
    }
}
