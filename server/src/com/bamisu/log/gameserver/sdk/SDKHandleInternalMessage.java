package com.bamisu.log.sdk;

import com.bamisu.gamelib.ExtensionHandleInternalMessage;
import com.bamisu.gamelib.base.BaseExtension;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Create by Popeye on 4:42 PM, 4/21/2020
 */
public class SDKHandleInternalMessage extends ExtensionHandleInternalMessage {
    public SDKHandleInternalMessage(BaseExtension extension) {
        super(extension);
    }

    @Override
    public Object handleInternalMessage(String cmdName, Object params) {
        ISFSObject rec = (ISFSObject) params;
        ISFSObject res = new SFSObject();
        switch (cmdName) {
//            case CMD.InternalMessage.LIST_KINGDOM_CONFIG:
//                res = (new CharacterCreatorToolsAPI()).getKingdomConfig(rec);
//                break;
        }
        return res;
    }
}
