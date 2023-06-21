package com.bamisu.log.gameserver.module.chat;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class ChatExtension extends BaseExtension {

    @Override
    public void init() {

        initModule();
    }

    @Override
    public void onServerReady() {
    }

    @Override
    public void initLogger() {
    }

    @Override
    public void initConfig() {
    }

    @Override
    public void initDB() {
    }

    @Override
    public void initLogic() {
    }

    @Override
    public void initModule() {
        new ChatHandler(this);
    }

    @Override
    public Object handleInternalMessage(String cmdName, Object params) {
        ISFSObject rec = (ISFSObject) params;
        ISFSObject res = new SFSObject();
        switch (cmdName){
            case CMD.InternalMessage.REMOVE_INFO_USER_CHAT:
                res = ((ChatHandler)getServerHandler(Params.Module.MODULE_CHAT)).RemoveInfoUserChat(rec);
                break;
            case CMD.InternalMessage.SEND_LOG_GUILD:
                ((ChatHandler)getServerHandler(Params.Module.MODULE_CHAT)).SendLogGuildChat(rec);
                break;
        }
        return res;
    }
}
