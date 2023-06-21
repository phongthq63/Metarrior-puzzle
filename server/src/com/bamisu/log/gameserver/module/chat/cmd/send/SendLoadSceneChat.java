package com.bamisu.log.gameserver.module.chat.cmd.send;

import com.bamisu.log.gameserver.datamodel.chat.entities.InfoMessage;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.chat.ChatHandler;
import com.bamisu.log.gameserver.module.chat.ChatRoomManager;
import com.bamisu.log.gameserver.module.chat.defind.EChatType;
import com.bamisu.log.gameserver.module.chat.entities.InfoUserMessage;
import com.bamisu.log.gameserver.module.chat.entities.PackageInfoMessage;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendLoadSceneChat extends BaseMsg {

    public ChatRoomManager manager;
    public List<PackageInfoMessage> pack;
    public Zone zone;

    public SendLoadSceneChat() {
        super(CMD.CMD_LOAD_SCENE_CHAT);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        ISFSArray tab = new SFSArray();
        ISFSObject tabObj;
        ISFSArray packMessage;
        ISFSObject messageObj;
        UserModel infoUser;
        for(PackageInfoMessage packTab : pack){
            tabObj = new SFSObject();

            tabObj.putInt(Params.TYPE, packTab.type);
            switch (EChatType.fromID(packTab.type)){
                case PRIVATE:
                    tabObj.putLong(Params.RECEIVER, packTab.message.uid);
                    infoUser = manager.getUserChat(packTab.message.uid);
                    tabObj.putInt(Params.LEVEL, manager.getLevelUser(infoUser.userID));
                    tabObj.putUtfString(Params.AVATAR_ID, infoUser.avatar);
                    tabObj.putInt(Params.FRAME, infoUser.avatarFrame);
                    tabObj.putUtfString(Params.NAME, infoUser.displayName);
                    break;
            }

            packMessage = new SFSArray();
            for(InfoMessage message : packTab.message.chat){
                messageObj = new SFSObject();
                infoUser = manager.getUserChat(message.uid);

                messageObj.putLong(Params.UID, message.uid);
                messageObj.putInt(Params.LEVEL, manager.getLevelUser(infoUser.userID));
                messageObj.putUtfString(Params.AVATAR_ID, infoUser.avatar);
                messageObj.putInt(Params.FRAME, infoUser.avatarFrame);
                messageObj.putUtfString(Params.NAME, infoUser.displayName);
                messageObj.putUtfString(Params.MESS, message.message);
                messageObj.putInt(Params.TIME, message.time);

                packMessage.addSFSObject(messageObj);
            }
            tabObj.putSFSArray(Params.MESS, packMessage);

            tab.addSFSObject(tabObj);
        }

        data.putSFSArray(Params.LIST, tab);
    }
}
