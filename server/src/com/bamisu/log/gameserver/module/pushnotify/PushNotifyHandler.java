package com.bamisu.log.gameserver.module.pushnotify;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.log.gameserver.module.pushnotify.cmd.rec.RecUpdatePushNotiID;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

/**
 * Create by Popeye on 3:56 PM, 12/28/2020
 */
public class PushNotifyHandler extends ExtensionBaseClientRequestHandler {
    public PushNotifyManager pushNotifyManager;

    public PushNotifyHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_PUSH_NOTIFY;;
        pushNotifyManager = new PushNotifyManager(this);
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_UPDATE_PUSH_NOTIFICATION_ID:
                handleUpdatePushNotiID(user, data);
                break;
        }
    }

    @WithSpan
    private void handleUpdatePushNotiID(User user, ISFSObject data) {
        RecUpdatePushNotiID recUpdatePushNotiID = new RecUpdatePushNotiID(data);
        UserModel userModel = ((BaseExtension) getParentExtension()).getUserManager().getUserModel(user);
        pushNotifyManager.handleUpdatePushNotiID(userModel.userID, recUpdatePushNotiID.platform, recUpdatePushNotiID.id);

    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_PUSH_NOTIFY, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_PUSH_NOTIFY, this);
    }
}
