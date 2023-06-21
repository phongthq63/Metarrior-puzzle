package com.bamisu.log.gameserver.module.chat.cmd.send;

import com.bamisu.log.gameserver.datamodel.chat.entities.InfoMessage;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.manager.UserManager;
import com.bamisu.log.gameserver.module.chat.ChatRoomManager;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetMessage extends BaseMsg {

    public ChatRoomManager manager;
    public UserManager userManager;
    public List<InfoMessage> listMessage;
    public Zone zone;

    public SendGetMessage() {
        super(CMD.CMD_GET_MESSAGE);
    }

    public SendGetMessage(short errorCode) {
        super(CMD.CMD_GET_MESSAGE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        ISFSArray packer = new SFSArray();
        ISFSObject messageObj;
        UserModel userModel;
        for(InfoMessage message : listMessage){
            messageObj = new SFSObject();

            userModel = userManager.getUserModel(message.uid);
            messageObj.putLong(Params.UID, message.uid);
            messageObj.putInt(Params.LEVEL, manager.getLevelUser(userModel.userID));
            messageObj.putUtfString(Params.AVATAR_ID, userModel.avatar);
            messageObj.putUtfString(Params.NAME, userModel.displayName);
            messageObj.putUtfString(Params.MESS, message.message);
            messageObj.putInt(Params.TIME, message.time);

            packer.addSFSObject(messageObj);
        }
        data.putSFSArray(Params.MESS, packer);
    }
}
