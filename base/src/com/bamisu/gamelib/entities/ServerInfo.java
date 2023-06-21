package com.bamisu.gamelib.entities;

import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Create by Popeye on 4:18 PM, 5/4/2020
 */
public class ServerInfo {
    public int serverID = -1;
    public String serverName = "";
    public String addr = "";
    public int port = -1;
    public int proxyPort = -1;
    public String zone = "";

    public ServerInfo() {
    }

    public ServerInfo(int serverID, String serverName, String addr, int port, int proxyPort, String zone) {
        this.serverID = serverID;
        this.serverName = serverName;
        this.addr = addr;
        this.port = port;
        this.proxyPort = proxyPort;
        this.zone = zone;
    }

    public ISFSObject toSFSObject(){
        ISFSObject isfsObject = new SFSObject();
        isfsObject.putUtfString(Params.ADDRESS, addr);
        isfsObject.putInt(Params.PORT, proxyPort);
        isfsObject.putUtfString(Params.ZONE, zone);
//        isfsObject.putInt(Params.SERVERID, serverID);
        return isfsObject;
    }
}
