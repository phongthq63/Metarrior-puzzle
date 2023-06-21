package com.bamisu.log.gameserver.datamodel.chat.entities;

import java.util.ArrayList;
import java.util.List;

public class PackInfoMessage {
    public long uid;
    public List<InfoMessage> chat = new ArrayList<>();

    public static PackInfoMessage create(long sender, List<InfoMessage> messages){
        PackInfoMessage packInfoMessage = new PackInfoMessage();
        packInfoMessage.uid = sender;
        packInfoMessage.chat = messages;

        return packInfoMessage;
    }
}
