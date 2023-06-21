package com.bamisu.log.gameserver.datamodel.chat.entities;

import com.bamisu.gamelib.utils.Utils;

public class InfoMessage implements IMessageChat {
    public long uid;
    public String message;
    public int time;

    public InfoMessage create(long uid, String message) {
        InfoMessage infoMessage = new InfoMessage();
        infoMessage.uid = uid;
        infoMessage.message = message;
        infoMessage.time = Utils.getTimestampInSecond();

        return infoMessage;
    }
}
