package com.bamisu.log.gameserver.module.item;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

public class ItemHandler extends ExtensionBaseClientRequestHandler {

    public ItemHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_ITEM;
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
        this.extension.addServerHandler(Params.Module.MODULE_ITEM, this);
    }
}
