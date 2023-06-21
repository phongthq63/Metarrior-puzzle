package com.bamisu.log.gameserver.module.sdkthriftclient;

import com.bamisu.log.sdkthrift.service.iap.IAPService;
import com.bamisu.log.sdkthrift.service.nft.NFTService;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

/**
 * Created by Quach Thanh Phong
 * On 3/12/2022 - 12:12 AM
 */
public class SDKGateNFT {
    public static final String serviceName = "nft";

    @WithSpan
    public static boolean haveInstanceTranferToken(String transactionHash) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            NFTService.Client client = (NFTService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result =  client.haveInstanceTranferToken(transactionHash);
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
    public static boolean saveTranferToken(String transactionHash, double count, long uid) throws TException {
        boolean result = false;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            NFTService.Client client = (NFTService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            result =  client.saveInstanceTranferToken(transactionHash, String.valueOf(count), uid);
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
