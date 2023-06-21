package com.bamisu.log.gameserver.module.sdkthriftclient;

import com.bamisu.gamelib.entities.JoinedServerData;
import com.bamisu.log.sdkthrift.service.multiserver.MultiServerService;
import com.bamisu.gamelib.entities.ServerInfo;
import com.bamisu.gamelib.utils.Utils;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

/**
 * Create by Popeye on 11:59 AM, 7/11/2020
 */
public class SDKGateMiltiServer {
    public static final String serviceName = "multiserver";

    @WithSpan
    public static boolean registerServer(int serverID, String serverName, String addr, int port, String zone) throws TException {
        boolean result;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            MultiServerService.Client client = (MultiServerService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result = client.registerServer(serverID, serverName, addr, port, zone);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }
        return result;
    }

    @WithSpan
    public static boolean unRegisterServer(int serverID) throws TException {
        boolean result;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            MultiServerService.Client client = (MultiServerService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result = client.unRegisterServer(serverID);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }
        return result;
    }


    /**
     *
     * @param serverID
     * @return
     * @throws TException
     */
    @WithSpan
    public static ServerInfo getServerInfo(int serverID) throws TException {
        ServerInfo result;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            MultiServerService.Client client = (MultiServerService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result = Utils.fromJson(client.getServerInfo(serverID), ServerInfo.class);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }
        return result;
    }

    @WithSpan
    public static int getServerCount() throws TException {
        int count = 0;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            MultiServerService.Client client = (MultiServerService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            count = client.getServerCount();
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }
        return count;
    }

    @WithSpan
    public static JoinedServerData getJoinedServer(String accountID) throws TException {
        JoinedServerData joinedServerData = null;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            MultiServerService.Client client = (MultiServerService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            joinedServerData = Utils.fromJson(client.getJoinedServer(accountID), JoinedServerData.class);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        } catch (TException e) {
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }
        return joinedServerData;
    }
}
