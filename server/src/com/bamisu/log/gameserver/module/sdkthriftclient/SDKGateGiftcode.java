package com.bamisu.log.gameserver.module.sdkthriftclient;

import com.bamisu.log.sdkthrift.entities.TActiveGiftcodeResult;
import com.bamisu.log.sdkthrift.service.giftcode.GiftcodeService;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

/**
 * Create by Popeye on 7:11 PM, 5/13/2020
 */
public class SDKGateGiftcode {
    public static final String serviceName = "giftcode";

    @WithSpan
    public static TActiveGiftcodeResult activeGiftcode(String code, int serverID, String userID, String accountID) throws TException {
        TActiveGiftcodeResult activeGiftcodeResult = null;
        TProtocol protocol = null;
        try {
            protocol = SDKThriftClient.createProtocol();
            GiftcodeService.Client client = (GiftcodeService.Client) SDKThriftClient.getServiceClient(protocol, serviceName);
            activeGiftcodeResult = client.activeGiftcode(code, serverID, userID, accountID);
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

        return activeGiftcodeResult;
    }
}
