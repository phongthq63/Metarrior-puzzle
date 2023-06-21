package com.bamisu.log.sdk.module.gamethriftclient.multiserver;

import com.bamisu.log.sdk.module.gamethriftclient.GameServerThriftClient;
import com.bamisu.gamelib.utils.Utils;
import com.log.bamisu.gamethrift.service.multiserver.MultiserverService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerGateMultiserver {
    public static final String serviceName = "multiserver";

    public static boolean maintenaceServer(String adr, int port, boolean PRE_MAINTENANCE) throws TException {
        boolean result = true;
        TProtocol protocol = null;
        try {
            protocol = GameServerThriftClient.createProtocol(adr, port);
            MultiserverService.Client client = (MultiserverService.Client) GameServerThriftClient.getServiceClient(protocol, serviceName);
            result =  client.maintenaceServer(PRE_MAINTENANCE);
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

    public static Map<String,Integer> getCCUServer(String adr, int port) throws TException {
        String resultJson;
        TProtocol protocol = null;
        try {
            protocol = GameServerThriftClient.createProtocol(adr, port);
            MultiserverService.Client client = (MultiserverService.Client) GameServerThriftClient.getServiceClient(protocol, serviceName);
            resultJson =  client.getCCUServer();
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

        Map<String,Integer> ccuZone = Utils.fromJson(resultJson, HashMap.class);

        return ccuZone.entrySet().parallelStream().
                collect(Collectors.toMap(obj -> obj.getKey().concat(":").concat(adr), obj -> obj.getValue(), (oldValue, newValue) -> newValue));
    }

    public static boolean updateEventModuleServer(String adr, int port, String zoneName, String module, boolean active, int timeStamp) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = GameServerThriftClient.createProtocol(adr, port);
            MultiserverService.Client client = (MultiserverService.Client) GameServerThriftClient.getServiceClient(protocol, serviceName);
            result =  client.activeEventModuleServer(module, active, timeStamp, zoneName);
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
