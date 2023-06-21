package com.bamisu.log.gameserver.module.chat.entities;

import com.bamisu.log.gameserver.datamodel.chat.entities.InfoMessage;
import com.bamisu.log.gameserver.datamodel.chat.entities.PackInfoMessage;
import com.bamisu.log.gameserver.module.chat.defind.EChatType;

import java.util.List;

public class PackageInfoMessage {
    public int type;
    public PackInfoMessage message;

    public PackageInfoMessage() {}

    public PackageInfoMessage(EChatType type, PackInfoMessage message) {
        this.type = type.getId();
        this.message = message;
    }
}
