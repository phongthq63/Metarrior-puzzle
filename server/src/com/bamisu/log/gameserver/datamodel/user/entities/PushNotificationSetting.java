package com.bamisu.log.gameserver.datamodel.user.entities;

import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Create by Popeye on 9:39 AM, 4/21/2020
 */
public class PushNotificationSetting {
    public boolean privateMessage = true;
    public boolean allianceChat = true;
    public boolean globalChat = false;
    public boolean chanelChat = false;

    public ISFSObject toSFSObject() {
        return SFSObject.newFromJsonData(Utils.toJson(this));
    }
}
