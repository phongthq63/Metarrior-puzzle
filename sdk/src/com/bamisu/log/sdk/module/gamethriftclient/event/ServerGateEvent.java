package com.bamisu.log.sdk.module.gamethriftclient.event;

import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.sdk.module.gamethriftclient.GameServerThriftClient;
import com.bamisu.log.sdk.module.gamethriftclient.event.entities.InfoEventNoti;
import com.log.bamisu.gamethrift.service.event.EventService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import java.util.List;

public class ServerGateEvent {
    public static final String serviceName = "event";



    public static boolean addEventGeneral(String addr, int port, String zoneName, InfoEventNoti eventData) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = GameServerThriftClient.createProtocol(addr, port);
            EventService.Client client = (EventService.Client) GameServerThriftClient.getServiceClient(protocol, serviceName);
            result =  client.addEvent(zoneName, Utils.toJson(eventData));
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

    public static boolean removeEventGeneral(String addr, int port, String zoneName, List<String> listIdEvent) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = GameServerThriftClient.createProtocol(addr, port);
            EventService.Client client = (EventService.Client) GameServerThriftClient.getServiceClient(protocol, serviceName);
            result =  client.removeEvent(zoneName, listIdEvent);
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

    public static boolean addEventSpecial(String addr, int port, String zoneName, InfoEventNoti eventData) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = GameServerThriftClient.createProtocol(addr, port);
            EventService.Client client = (EventService.Client) GameServerThriftClient.getServiceClient(protocol, serviceName);
            result =  client.addSpecialEvent(zoneName, Utils.toJson(eventData));
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

    public static boolean removeEventSpecial(String addr, int port, String zoneName, List<String> listIdEvent) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = GameServerThriftClient.createProtocol(addr, port);
            EventService.Client client = (EventService.Client) GameServerThriftClient.getServiceClient(protocol, serviceName);
            result =  client.removeSpecialEvent(zoneName, listIdEvent);
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
