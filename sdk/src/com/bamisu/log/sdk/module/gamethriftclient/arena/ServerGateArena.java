package com.bamisu.log.sdk.module.gamethriftclient.arena;

import com.bamisu.log.sdk.module.gamethriftclient.GameServerThriftClient;
import com.log.bamisu.gamethrift.service.arena.ArenaService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

public class ServerGateArena {
    public static final String serviceName = "arena";



    public static boolean sendGiftArenaDaily(String addr, int port, String zoneName) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = GameServerThriftClient.createProtocol(addr, port);
            ArenaService.Client client = (ArenaService.Client) GameServerThriftClient.getServiceClient(protocol, serviceName);
            result =  client.sendGiftArenaDaily(zoneName);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        }catch (TException e){
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }

        return result;
    }

    public static boolean closeSeasonArena(String addr, int port, String zoneName) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = GameServerThriftClient.createProtocol(addr, port);
            ArenaService.Client client = (ArenaService.Client) GameServerThriftClient.getServiceClient(protocol, serviceName);
            result =  client.closeSeasonArena(zoneName);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        }catch (TException e){
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }

        return result;
    }

    public static boolean openSeasonArena(String addr, int port, String zoneName) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = GameServerThriftClient.createProtocol(addr, port);
            ArenaService.Client client = (ArenaService.Client) GameServerThriftClient.getServiceClient(protocol, serviceName);
            result =  client.openSeasonArena(zoneName);
            protocol.getTransport().close();

            if (protocol != null) {
                protocol.getTransport().close();
            }
        }catch (TException e){
            e.printStackTrace();
            if (protocol != null) {
                protocol.getTransport().close();
            }
            throw e;
        }

        return result;
    }
}
