package com.bamisu.log.gameserver.module.darkgate.model.entities;

import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Create by Popeye on 4:50 PM, 11/26/2020
 */
public class DarkRealmLogVO {
    public int time;
    public long point;

    public DarkRealmLogVO() {
    }

    public DarkRealmLogVO(int time, long point) {
        this.time = time;
        this.point = point;
    }

    public ISFSObject toSFSObject(){
        ISFSObject isfsObject = new SFSObject();
        isfsObject.putInt(Params.TIME, time);
        isfsObject.putLong(Params.POINT, point);
        return isfsObject;
    }
}
