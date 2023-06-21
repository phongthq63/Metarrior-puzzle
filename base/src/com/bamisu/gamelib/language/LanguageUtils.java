package com.bamisu.gamelib.language;

import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

/**
 * Create by Popeye on 10:23 AM, 7/10/2019
 */
public class LanguageUtils {
    public static final String SYMBOL = "|";
    public static ISFSObject config = loadConfig();

    private static ISFSObject loadConfig() {
        return SFSObject.newFromJsonData(Utils.loadConfig("language/template.json"));
    }

    public static String toText(String id, String template){
        return "";
    }

    public static String toTemplate(String id, List<Object> params){
        String s = id + "";
        for(Object tmp : params){
            s += SYMBOL + String.valueOf(tmp);
        }

        return s;
    }

    public static String toTemplate(String id){
        return id;
    }
}
