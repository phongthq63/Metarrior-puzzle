package com.bamisu.log.gameserver.module.chat.cmd.send;

import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.chat.ChatRoomManager;
import com.smartfoxserver.v2.entities.Zone;

public class SendSendMessage extends BaseMsg {

    public ChatRoomManager manager;
    public UserModel userModel;
    public long receider;
    public String message;
    public int type;

    public SendSendMessage() {
        super(CMD.CMD_SEND_MESSAGE);
    }

    public SendSendMessage(short errorCode) {
        super(CMD.CMD_SEND_MESSAGE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putLong(Params.SENDER, userModel.userID);
        data.putLong(Params.RECEIVER, receider);
        data.putUtfString(Params.AVATAR_ID, userModel.avatar);
        data.putInt(Params.FRAME, userModel.avatarFrame);
        data.putUtfString(Params.NAME, userModel.displayName);
        data.putInt(Params.LEVEL, manager.getLevelUser(userModel.userID));
        data.putUtfString(Params.MESS, message);
        data.putInt(Params.TYPE, type);
        data.putInt(Params.TIME, Utils.getTimestampInSecond());
    }
}
