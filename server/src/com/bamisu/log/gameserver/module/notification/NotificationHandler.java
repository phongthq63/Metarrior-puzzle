package com.bamisu.log.gameserver.module.notification;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.log.gameserver.module.notification.cmd.rec.RecRemoveNotify;
import com.bamisu.log.gameserver.module.notification.cmd.send.SendGetAllNotify;
import com.bamisu.log.gameserver.module.notification.cmd.send.SendRemoveNotify;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.concurrent.ScheduledExecutorService;

public class NotificationHandler extends ExtensionBaseClientRequestHandler {

    public NotificationHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_NOTIFICATION;
        new NotificationGameEventHandle(extension.getParentZone());
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_GET_ALL_NOTIFY:
                doGetAllNotify(user, data);
                break;
            case CMD.CMD_REMOVE_NOTIFY:
                doRemoveNotify(user, data);
                break;
        }
    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_NOTIFICATION, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_NOTIFICATION, this);
    }



    /*----------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------*/
    @WithSpan
    private void doGetAllNotify(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        SendGetAllNotify objPut = new SendGetAllNotify();
        objPut.listNotify = NotificationManager.getInstance().getListNotifyIDLogin(uid, getParentExtension().getParentZone());
        send(objPut, user);
    }

    @WithSpan
    private void doRemoveNotify(User user, ISFSObject data){
        long uid = extension.getUserManager().getUserModel(user).userID;

        RecRemoveNotify objGet = new RecRemoveNotify(data);

        //Xoa
        if(!NotificationManager.getInstance().removeNotifyID(uid, objGet.listNotify, getParentExtension().getParentZone())){
            SendRemoveNotify objPut = new SendRemoveNotify(ServerConstant.ErrorCode.ERR_SYS);
            send(objPut, user);
        }

        SendRemoveNotify objPut = new SendRemoveNotify();
        send(objPut, user);
    }
}
