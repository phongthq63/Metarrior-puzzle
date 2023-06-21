package com.bamisu.gamelib.utils;

import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.SFSArray;

/**
 * Create by Popeye on 11:55 AM, 12/10/2020
 */
public class IngameUtils {
    public static ISFSArray tableArray = SFSArray.newFromJsonData(Utils.loadConfig("campaign/table.json"));
}
