package com.bamisu.log.gameserver.module.user.cmd.receive;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * Create by Popeye on 9:52 AM, 4/21/2020
 */
public class RecUpdatePushNotificationSetting extends BaseCmd {
    public int privateMessage = 2;
    public int allianceChat = 2;
    public int globalChat = 2;
    public int chanelChat = 2;

    public RecUpdatePushNotificationSetting(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        if(data.containsKey("privateMessage")) privateMessage = data.getBool("privateMessage") ? 1 : 0;
        if(data.containsKey("allianceChat")) allianceChat = data.getBool("allianceChat") ? 1 : 0;
        if(data.containsKey("globalChat")) globalChat = data.getBool("globalChat") ? 1 : 0;
        if(data.containsKey("chanelChat")) chanelChat = data.getBool("chanelChat") ? 1 : 0;
    }
}
