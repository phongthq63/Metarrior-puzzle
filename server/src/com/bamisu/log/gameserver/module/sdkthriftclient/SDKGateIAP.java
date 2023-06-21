package com.bamisu.log.gameserver.module.sdkthriftclient;

import com.bamisu.log.sdkthrift.service.iap.IAPService;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

public class SDKGateIAP {
    public static final String serviceName = "iap";

    @WithSpan
    public static boolean haveInstanceBuyIAP(String accountID, String purchaseToken) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            IAPService.Client client = (IAPService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result =  client.haveInstanceBuyIAP(accountID, purchaseToken);
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

    @WithSpan
    public static boolean saveBuyIAP(String accountID, String purchaseToken) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            IAPService.Client client = (IAPService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result =  client.saveInstanceBuyIAP(accountID, purchaseToken);
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
