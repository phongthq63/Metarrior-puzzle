package com.bamisu.gamelib.base;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

/**
 * Create by Popeye on 5:27 PM, 7/12/2019
 */
public class ServerEventHandler extends ExtensionBaseClientRequestHandler {
    public ServerEventHandler(BaseExtension extension) {
        super(extension);
    }

    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {

    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {

    }

    @Override
    protected void initHandlerServerEvent() {

    }
}
