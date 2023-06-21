package com.bamisu.gamelib.entities;

import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.SFSArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by Popeye on 10:38 AM, 9/22/2020
 */
public class JoinedServerData {
    public Map<Integer, JoinedServerInfo> map = new HashMap<>();

    public JoinedServerData() {
    }

    public JoinedServerData(Map<Integer, JoinedServerInfo> map) {
        this.map = map;
    }

    public ISFSArray readAsSFSArray(){
        return SFSArray.newFromJsonData(Utils.toJson(map.values()));
    }
}
