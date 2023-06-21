package com.bamisu.gamelib.base.event._interface;

import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Popeye on 7/6/2017.
 */
public interface IInternalMessage {
    short getType();
    ISFSObject getParams();
}
