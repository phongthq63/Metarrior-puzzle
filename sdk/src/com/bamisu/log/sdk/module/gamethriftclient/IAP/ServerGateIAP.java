package com.bamisu.log.sdk.module.gamethriftclient.IAP;

import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.sdk.module.gamethriftclient.GameServerThriftClient;
import com.bamisu.log.sdk.module.gamethriftclient.IAP.entities.InfoIAPSale;
import com.log.bamisu.gamethrift.service.IAP.IAPClientService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import java.util.List;

public class ServerGateIAP {
    public static final String serviceName = "iap";

    public static boolean addSaleIAP(String addr, int port, String zoneName, InfoIAPSale saleData) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = GameServerThriftClient.createProtocol(addr, port);
            IAPClientService.Client client = (IAPClientService.Client) GameServerThriftClient.getServiceClient(protocol, serviceName);
            result =  client.addSaleIAP(zoneName, Utils.toJson(saleData));
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

    public static boolean removeSaleIAP(String addr, int port, String zoneName, List<String> listIdSale) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = GameServerThriftClient.createProtocol(addr, port);
            IAPClientService.Client client = (IAPClientService.Client) GameServerThriftClient.getServiceClient(protocol, serviceName);
            result =  client.removeSaleIAP(zoneName, listIdSale);
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

    public static boolean buyIAP(String addr, int port, String zoneName, long uid, String idPackage) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = GameServerThriftClient.createProtocol(addr, port);
            IAPClientService.Client client = (IAPClientService.Client) GameServerThriftClient.getServiceClient(protocol, serviceName);
            result =  client.buyIAP(zoneName, uid, idPackage);
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
