package com.bamisu.log.gameserver.module.chat.cmd.send;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.chat.entities.InfoMessage;
import com.bamisu.log.gameserver.datamodel.guild.entities.LogGuildInfo;
import com.bamisu.log.gameserver.module.chat.ChatRoomManager;
import com.bamisu.log.gameserver.module.chat.defind.EChatType;
import com.bamisu.log.gameserver.module.chat.entities.PackageInfoMessage;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class SendLoadSceneChatGuild extends BaseMsg {

    public ChatRoomManager manager;
    public List<PackageInfoMessage> pack;
    public List<LogGuildInfo> log;
    public Zone zone;


    public SendLoadSceneChatGuild() {
        super(CMD.CMD_LOAD_SCENE_CHAT_GUILD);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError()) return;

        ISFSArray packer = new SFSArray();
        ISFSObject messageObj;
        UserModel userModel;
        for(PackageInfoMessage tab : pack){
            if(tab.type != EChatType.GUILD.getId()) continue;

            for(InfoMessage message : tab.message.chat){
                messageObj = new SFSObject();

                userModel = manager.getUserChat(message.uid);
                messageObj.putLong(Params.UID, message.uid);
                messageObj.putInt(Params.LEVEL, manager.getLevelUser(userModel.userID));
                messageObj.putUtfString(Params.AVATAR_ID, userModel.avatar);
                messageObj.putUtfString(Params.NAME, userModel.displayName);
                messageObj.putUtfString(Params.MESS, message.message);
                messageObj.putInt(Params.TIME, message.time);

                packer.addSFSObject(messageObj);
            }
        }
        data.putSFSArray(Params.MESS, packer);

        data.putSFSArray(Params.LOGS, SFSArray.newFromJsonData(Utils.toJson(log)));
    }
}
