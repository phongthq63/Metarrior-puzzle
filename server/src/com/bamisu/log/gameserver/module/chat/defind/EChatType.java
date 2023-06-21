package com.bamisu.log.gameserver.module.chat.defind;

import com.bamisu.log.gameserver.module.chat.ChatManager;
import com.bamisu.log.gameserver.module.chat.config.entities.ChatVO;

public enum EChatType {
    GLOBAL(ChatManager.getInstance().getChatConfigDependType("global")),
    CHANNEL(ChatManager.getInstance().getChatConfigDependType("channel")),
    GUILD(ChatManager.getInstance().getChatConfigDependType("guild")),
    PRIVATE(ChatManager.getInstance().getChatConfigDependType("private"));

    int id;
    int limitTimeSend;
    int limitTimeSave;
    int limitMessageSave;

    public int getId() {
        return id;
    }

    public int getLimitTimeSend() {
        return limitTimeSend;
    }

    public int getLimitTimeSave() {
        return limitTimeSave;
    }

    public int getLimitMessageSave() {
        return limitMessageSave;
    }

    EChatType(ChatVO chatConfig) {
        this.id = chatConfig.id;
        this.limitTimeSend = chatConfig.distanceSend;
        this.limitTimeSave = chatConfig.limitTime;
        this.limitMessageSave = chatConfig.limitMessage;
    }

    public static EChatType fromID(int id){
        for(EChatType type : EChatType.values()){
            if(type.id == id){
                return type;
            }
        }
        return null;
    }
}
