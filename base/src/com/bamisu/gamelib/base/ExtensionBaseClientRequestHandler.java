package com.bamisu.gamelib.base;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.business.Debug;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.IServerEventHandler;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.List;

/**
 * Created by Popeye on 6/20/2017.
 */
public abstract class ExtensionBaseClientRequestHandler extends BaseClientRequestHandler implements IServerEventHandler {
    protected String MODULE_ID = "default";
    protected BaseExtension extension;
    public ExtensionBaseClientRequestHandler(BaseExtension extension) {
        super();
        this.extension = extension;
        initHandlerClientRequest();
        initHandlerServerEvent();
    }

    public String getMODULE_ID() {
        return MODULE_ID;
    }

    @WithSpan
    @Override
    public final void handleClientRequest(User user, ISFSObject isfsObject) {
        int cmdId = isfsObject.getInt(Params.CMD_ID);
        doClientRequest(cmdId, user, isfsObject);
    }

    protected abstract void doClientRequest(int cmdId, User user, ISFSObject data);

    @WithSpan
    public void send(BaseMsg msg, User recipient) {

        msg.packData();
        super.send(this.MODULE_ID, msg.getData(), recipient);
        Debug.traceOutPackage("OUT PACKAGE: " + Utils.byteToKByte(msg.getData().toBinary().length) + " " + msg.getData().toJson());
    }

    @WithSpan
    public void send(BaseMsg msg, List<User> recipients) {
        msg.packData();
        super.send(this.MODULE_ID, msg.getData(), recipients);
        Debug.traceOutPackage("OUT PACKAGE: " + Utils.byteToKByte(msg.getData().toBinary().length) + " " + msg.getData().toJson());
    }

    @WithSpan
    public void send(BaseMsg msg, User recipient, boolean UDP) {
        msg.packData();
        super.send(this.MODULE_ID, msg.getData(), recipient, UDP);
        Debug.traceOutPackage("OUT PACKAGE: " + Utils.byteToKByte(msg.getData().toBinary().length) + " " + msg.getData().toJson());
    }

    @WithSpan
    public void send(BaseMsg msg, List<User> recipients, boolean UDP) {
        msg.packData();
        super.send(this.MODULE_ID, msg.getData(), recipients, UDP);
        Debug.traceOutPackage("OUT PACKAGE: " + msg.getData().toBinary().length * 1.0 / 1024 + " " + msg.getData().toJson());
    }

    @WithSpan
    public void send(BaseMsg msg, ISession session) {
        User user = ExtensionUtility.getInstance().getUserBySession(session);
        send(msg, user);
        Debug.traceOutPackage("OUT PACKAGE: " + msg.getData().toBinary().length * 1.0 / 1024 + " " + msg.getData().toJson());
    }

    @WithSpan
    @Override
    public final void handleServerEvent(ISFSEvent isfsEvent) throws SFSException {
            SFSEventType type = isfsEvent.getType();
            doServerEvent(type, isfsEvent);
    }

    protected abstract void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException;

    protected abstract void initHandlerClientRequest();

    protected abstract void initHandlerServerEvent();

}
