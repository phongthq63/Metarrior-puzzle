package com.bamisu.log.sdk.module.sdkthriftserver.handler.multiserver;

import com.bamisu.log.sdk.module.multiserver.MultiServerManager;
import com.bamisu.gamelib.entities.ServerInfo;
import com.bamisu.log.sdkthrift.exception.ThriftSVException;
import com.bamisu.log.sdkthrift.service.multiserver.MultiServerService;
import com.bamisu.gamelib.utils.Utils;
import org.apache.thrift.TException;

/**
 * Create by Popeye on 10:20 AM, 7/11/2020
 */
public class TMultiServerService implements MultiServerService.Iface {
    @Override
    public boolean registerServer(int serverID, String serverName, String addr, int port, String zone) throws ThriftSVException, TException {
        //System.out.println("Register server: serverID:" + serverID + ",serverName:" + serverName + ",String:" + addr + ",port:" + port + ",zone:" + zone);
        return MultiServerManager.getInstance().registerServer(serverID, serverName, addr, port, zone);
    }

    @Override
    public boolean unRegisterServer(int serverID) throws ThriftSVException, TException {
        //System.out.println("Unregister server id:" + serverID);
        return MultiServerManager.getInstance().unRegisterServer(serverID);
    }

    @Override
    public String getServerInfo(int serverID) throws ThriftSVException, TException {
        ServerInfo serverInfo = MultiServerManager.getInstance().getServerInfo(serverID);
        if(serverInfo == null) throw new ThriftSVException();
        return serverInfo.toSFSObject().toJson();
    }

    @Override
    public int getServerCount() throws ThriftSVException, TException {
        return MultiServerManager.getInstance().getServerCount();
    }

    @Override
    public String getJoinedServer(String accountID) throws ThriftSVException, TException {
        return MultiServerManager.getInstance().getJoinedServer(accountID);
    }
}
