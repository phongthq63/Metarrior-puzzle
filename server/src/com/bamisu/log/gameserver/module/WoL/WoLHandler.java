package com.bamisu.log.gameserver.module.WoL;

import com.bamisu.log.gameserver.module.WoL.cmd.receive.RecWoLReceiveReward;
import com.bamisu.log.gameserver.module.WoL.cmd.receive.RecWoLUserAchievement;
import com.bamisu.log.gameserver.module.WoL.cmd.receive.RecWoLGetRank;
import com.bamisu.log.gameserver.module.WoL.defines.WoLAreaDefines;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

public class WoLHandler extends ExtensionBaseClientRequestHandler {
    WoLManager woLManager;
    public WoLHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_WOL;
        woLManager = WoLManager.getInstance();
        woLManager.setWoLHandle(this);
        WoLGameEventHandler woLGameEventHandler = new WoLGameEventHandler(extension.getParentZone());
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_WOL_GET_RANK:
                handleWoLGetRank(user, data);
                break;
            case CMD.CMD_WOL_USER_ACHIEVEMENT:
                handleWoLUserAchievement(user, data);
                break;
            case CMD.CMD_WOL_RECEIVE_REWARD:
                handleWoLReceiveReward(user, data);
                break;
        }
    }

    @WithSpan
    private void handleWoLReceiveReward(User user, ISFSObject data) {
        RecWoLReceiveReward rec = new RecWoLReceiveReward(data);
        rec.unpackData();
        woLManager.receiveReward(extension, user, rec.area, rec.stage, rec.challenge);
    }

    @WithSpan
    private void handleWoLUserAchievement(User user, ISFSObject data) {
        RecWoLUserAchievement rec = new RecWoLUserAchievement(data);
        rec.unpackData();
        woLManager.getListUserAchievement(user, extension, WoLAreaDefines.fromID(rec.area), rec.stage);
    }

    @WithSpan
    private void handleWoLGetRank(User user, ISFSObject data) {
        RecWoLGetRank rec = new RecWoLGetRank(data);
        rec.unpackData();
        woLManager.getRank(extension, user);
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_WOL, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_WOL, this);
    }
}
