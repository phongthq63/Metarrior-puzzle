package com.bamisu.gamelib.http.entities.response;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

/**
 * Created by Popeye on 11/6/2017.
 */
public class ResPotInfo{

    public SFSObject data = new SFSObject();
    public SFSArray list = new SFSArray();
    public SFSObject event = new SFSObject();

    public void add(int gId, List<Long> pots){
        SFSObject obj  = new SFSObject();
        obj.putInt(Params.GAME_ID, gId);
        obj.putLongArray(Params.LIST, pots);
        list.addSFSObject(obj);
    }

    public String toJson(){
        this.data.putSFSArray(Params.LIST, list);
        this.data.putSFSObject("event", event);
        return this.data.toJson();
    }
}
