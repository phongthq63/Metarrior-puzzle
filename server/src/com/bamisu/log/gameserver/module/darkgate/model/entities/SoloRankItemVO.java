package com.bamisu.log.gameserver.module.darkgate.model.entities;

import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Comparator;

/**
 * Create by Popeye on 9:49 AM, 11/18/2020
 */
public class SoloRankItemVO{
    public long uid;
    public String name;
    public String avatar;
    public int frame;
    public int level;
    public int power;
    public long point;

    public SoloRankItemVO() {
    }

    public SoloRankItemVO(long uid, String name, String avatar, int frame, int level, int power, long point) {
        this.uid = uid;
        this.name = name;
        this.avatar = avatar;
        this.frame = frame;
        this.level = level;
        this.power = power;
        this.point = point;
    }

    public ISFSObject toSFSObject(){
        ISFSObject isfsObject = new SFSObject();
        isfsObject.putUtfString(Params.NAME, name);
        isfsObject.putUtfString(Params.AVATAR_ID, avatar);
        isfsObject.putInt(Params.AVATAR_FRAME, frame);
        isfsObject.putInt(Params.POWER, power);
        isfsObject.putLong(Params.POINT, point);
        isfsObject.putInt(Params.LEVEL, level);
        return isfsObject;
    }
}
