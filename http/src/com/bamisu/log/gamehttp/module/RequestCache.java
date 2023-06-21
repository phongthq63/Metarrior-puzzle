package com.bamisu.log.gamehttp.module;

import com.bamisu.gamelib.http.entities.RequestInfo;
import com.bamisu.gamelib.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Create by Popeye on 4:00 PM, 9/3/2019
 */
public class RequestCache {
    private static RequestCache ourInstance = new RequestCache();

    public static RequestCache getInstance() {
        return ourInstance;
    }

    private Map<String, RequestInfo> requestIPCache;
    private Map<String, Integer> countSpam;

    private RequestCache() {
        requestIPCache = new HashMap<>();
        countSpam = new HashMap<>();
    }

    public boolean check(String ip) {
        if (!countSpam.containsKey(ip)) {
            countSpam.put(ip, 0);
        }

        if (countSpam.get(ip) > 20) {
            return false;
        }

        RequestInfo requestInfo = new RequestInfo();
        requestInfo.lastTime = Utils.getTimestampInSecond();

        if (requestIPCache.containsKey(ip)) {
            if (requestInfo.lastTime - requestIPCache.get(ip).lastTime < 10) {
                countSpam.put(ip, countSpam.get(ip) + 1);
                return false;
            }
        }

        requestIPCache.put(ip, requestInfo);
        return true;
    }
}
