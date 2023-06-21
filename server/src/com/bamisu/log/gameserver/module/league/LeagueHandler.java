package com.bamisu.log.gameserver.module.league;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

/**
 * Created by Quach Thanh Phong
 * On 11/11/2022 - 9:53 PM
 */
public class LeagueHandler extends ExtensionBaseClientRequestHandler {

    public LeagueHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_LEAGUE;
        new LeagueGameEventHandler(extension.getParentZone());
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
