package com.bamisu.log.gameserver.module.user.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendGetChannelList extends BaseMsg {

    public User user;
    public List<Room> listRoom;

    public SendGetChannelList() {
        super(CMD.CMD_GET_CHANNEL_LIST);
    }

    public SendGetChannelList(short errorCode) {
        super(CMD.CMD_GET_CHANNEL_LIST, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        ISFSArray packList = new SFSArray();
        ISFSObject roomPack;
        for(Room room : listRoom){
            roomPack = new SFSObject();
            roomPack.putUtfString(Params.ID, room.getName());
            roomPack.putUtfString(Params.NAME, room.getVariable(Params.NAME).getStringValue());
            roomPack.putBool(Params.FULL, room.isFull());
            roomPack.putBool(Params.IN, room.containsUser(user));

            packList.addSFSObject(roomPack);
        }
        data.putSFSArray(Params.LIST, packList);
    }
}
