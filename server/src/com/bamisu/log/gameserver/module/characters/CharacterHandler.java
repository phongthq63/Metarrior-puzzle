package com.bamisu.log.gameserver.module.characters;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import org.apache.log4j.Logger;

public class CharacterHandler extends ExtensionBaseClientRequestHandler {

    private Logger logger = Logger.getLogger(CharacterHandler.class);    //Get log smartfox

    public CharacterHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_CHARACTER;

        extension.trace("Zone " + extension.getParentZone().getName() + " CharacterHandler init ");
    }

    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {

    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {

    }

    @Override
    protected void initHandlerClientRequest() {
        //this.extension.addRequestHandler(Params.Module.MODULE_CHARACTER, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addServerHandler(Params.Module.MODULE_CHARACTER, this);
    }
}
