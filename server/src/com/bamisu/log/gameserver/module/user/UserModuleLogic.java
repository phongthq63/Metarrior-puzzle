package com.bamisu.log.gameserver.module.user;

import com.bamisu.gamelib.base.data.BaseMsg;
import com.smartfoxserver.v2.entities.User;

/**
 * Create by Popeye on 11:25 AM, 7/11/2019
 */
public class UserModuleLogic {
    public UserHandler handler;
    public UserModuleLogic(UserHandler handler) {
        this.handler = handler;
    }
    public void send(BaseMsg baseMsg, User user) {
        handler.send(baseMsg, user);
    }
}
