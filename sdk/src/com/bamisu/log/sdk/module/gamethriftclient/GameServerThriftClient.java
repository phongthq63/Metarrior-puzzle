package com.bamisu.log.sdk.module.gamethriftclient;

import com.log.bamisu.gamethrift.service.IAP.IAPClientService;
import com.log.bamisu.gamethrift.service.arena.ArenaService;
import com.log.bamisu.gamethrift.service.event.EventService;
import com.log.bamisu.gamethrift.service.mail.MailService;
import com.log.bamisu.gamethrift.service.multiserver.MultiserverService;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Create by Popeye on 11:09 AM, 4/25/2020
 */
public class GameServerThriftClient {

    public static TTransport createTransport(String url, int port) {
        return new TFramedTransport(new TSocket(url, port));
    }

    public static TBinaryProtocol createProtocol(String url, int port) throws TTransportException {
        TTransport transport = createTransport(url, port);
        transport.open();
        return new TBinaryProtocol(transport);
    }

    public static TServiceClient getServiceClient(TProtocol protocol, String serviceName) throws TTransportException {
        TMultiplexedProtocol mp = new TMultiplexedProtocol(protocol, serviceName);
        return getServiceByName(serviceName, mp);
    }

    private static TServiceClient getServiceByName(String serviceName, TProtocol protocol) {
        switch (serviceName) {
            case "multiserver":
                return new MultiserverService.Client(protocol);
            case "mail":
                return new MailService.Client(protocol);
            case "iap":
                return new IAPClientService.Client(protocol);
            case "event":
                return new EventService.Client(protocol);
            case "arena":
                return new ArenaService.Client(protocol);
        }
        return null;
    }
}
