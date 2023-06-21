package com.bamisu.gamelib.httpserver;

import java.util.Map;
import java.util.TreeMap;

public class AppContext {
    public int userid = 0;
    public String username = "";
    public String avatarVersion = "";
    public String displayName = "";
    public byte gender;
    public String email = "";
    public String serverIp = "";
    public String requestDomain = "";
    public String requestUri = "";
    public String clientIp = "";
    public boolean isDev = false;
    public boolean isMobile = false;
    public Map cache = new TreeMap();
}
