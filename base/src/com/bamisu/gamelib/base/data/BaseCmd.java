package com.bamisu.gamelib.base.data;

import com.bamisu.gamelib.utils.business.Debug;
import com.bamisu.gamelib.utils.business.Debug;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Created by Popeye on 4/15/2017.
 */
public abstract class BaseCmd {
    public ISFSObject data;

    public BaseCmd(ISFSObject data) {
        this.data = data;
        Debug.traceInPackage("IN PACKAGE : " + data.toJson());
    }
    public abstract void unpackData();
}
