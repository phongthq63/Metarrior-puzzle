package com.bamisu.gamelib.base.event;

import com.bamisu.gamelib.base.event._interface.IInternalMessage;
import com.bamisu.gamelib.base.event._interface.IInternalMessage;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Popeye on 7/6/2017.
 */
public class InternalMessage implements IInternalMessage {

    private final short type;
    private final ISFSObject params;

    public InternalMessage(short internalMessageType) {
        this(internalMessageType, null);
    }

    public InternalMessage(short internalMessageType, ISFSObject params) {
        this.type = internalMessageType;
        this.params = params;
    }

    @Override
    public short getType() {
        return this.type;
    }

    @Override
    public ISFSObject getParams() {
        return params;
    }

}
