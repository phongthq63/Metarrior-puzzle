package com.bamisu.log.gameserver.module.mail;

import com.bamisu.log.gameserver.module.mail.cmd.receive.*;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;

public class MailHandler extends ExtensionBaseClientRequestHandler {
    public MailHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_MAIL;
    }

    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_NEW_MAIL:
                handleSendMail(user, data);
                break;
            case CMD.CMD_READ_MAIL:
                handleReadMail(user, data);
                break;
            case CMD.CMD_CONFIRM_MAIL:
                handleCollectMail(user, data);
                break;
            case CMD.CMD_COLLECT_ALL_MAIL:
                handleCollectAllMail(user, data);
                break;
            case CMD.CMD_DELETE_ALL_MAIL: //checked
                handleDeleteAllMail(user, data);
                break;
            case CMD.CMD_GET_LIST_MAIL: //checked
                handleGetListMail(user, data);
                break;
        }
    }

    @WithSpan
    private void handleGetListMail(User user, ISFSObject data) {
        RecGetListMail rec = new RecGetListMail(data);
        rec.unpackData();
        UserModel um = extension.getUserManager().getUserModel(user);
        MailManager.getInstance().getListMail(this, um.userID, user);
    }

    @WithSpan
    private void handleDeleteAllMail(User user, ISFSObject data) {
        RecDeleteAllMail rec = new RecDeleteAllMail(data);
        rec.unpackData();
        UserModel um = extension.getUserManager().getUserModel(user);
        MailManager.getInstance().clearMail(this, user, um.userID);
    }

    @WithSpan
    private void handleCollectAllMail(User user, ISFSObject data) {
        RecCollectAllMail rec = new RecCollectAllMail(data);
        rec.unpackData();
        long uid = extension.getUserManager().getUserModel(user).userID;
        MailManager.getInstance().collectAllMail(this, user, uid);
    }

    @WithSpan
    private void handleCollectMail(User user, ISFSObject data) {
        RecCollectMail rec = new RecCollectMail(data);
        rec.unpackData();
        long uid = extension.getUserManager().getUserModel(user).userID;
        MailManager.getInstance().collectMail(this, user, uid, rec.idMail);
    }

    @WithSpan
    private void handleReadMail(User user, ISFSObject data) {
        RecReadMail rec = new RecReadMail(data);
        rec.unpackData();
        UserModel um = extension.getUserManager().getUserModel(user);
        MailManager.getInstance().readMail(this, rec.id, um.userID, user);
    }

    //-----TEST-----//
    @WithSpan
    private void handleSendMail(User user, ISFSObject data) {
//        UserModel um, User user, Zone zone, List< Resource > listGift, int ozil, int leno
//        UserModel um = extension.getUserManager().getUserModel(user);
//        ResourcePackage resource = new ResourcePackage("SPI1021", 999);
//        List<ResourcePackage> list = new ArrayList<>();
//        list.add(resource);
//        MailUtils.getInstance().sendTestMail(um.userID, user, list, 123, 123);
    }


    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_MAIL, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_MAIL, this);
    }
}
