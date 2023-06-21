package com.bamisu.gamelib;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.BaseExtension;

/**
 * Create by Popeye on 5:40 PM, 10/21/2019
 */
public abstract class ExtensionHandleInternalMessage {
    public BaseExtension extension;

    public ExtensionHandleInternalMessage(BaseExtension extension){
        this.extension = extension;
    }

    abstract public Object handleInternalMessage(String cmdName, Object params);
}
