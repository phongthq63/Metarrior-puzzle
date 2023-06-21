package com.bamisu.log.gameserver.module.darkgate.model.entities;

import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Create by Popeye on 9:52 AM, 11/18/2020
 */
public class AllianceRankItemVO {
    public long aid;
    public String name;
    public String avatar1 = "pat0_sym0";
    public int level;
    public long point;

    public AllianceRankItemVO() {
    }

    public AllianceRankItemVO(long aid, String name, String avatar, int level, long point) {
        this.aid = aid;
        this.name = name;
        this.avatar1 = avatar;
        this.level = level;
        this.point = point;
    }

    public ISFSObject toSFSObject(){
        ISFSObject isfsObject = new SFSObject();
        isfsObject.putUtfString(Params.NAME, name);
        isfsObject.putUtfString(Params.AVATAR_ID, avatar1);
        isfsObject.putLong(Params.POINT, point);
        isfsObject.putInt(Params.LEVEL, level);
        return isfsObject;
    }
}
