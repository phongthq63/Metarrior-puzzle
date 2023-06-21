package com.bamisu.log.gameserver.module.sdkthriftclient;

import com.bamisu.log.sdkthrift.service.account.AccountService;
import com.bamisu.log.sdkthrift.service.giftcode.GiftcodeService;
import com.bamisu.log.sdkthrift.service.iap.IAPService;
import com.bamisu.log.sdkthrift.service.invitecode.InviteService;
import com.bamisu.log.sdkthrift.service.multiserver.MultiServerService;
import com.bamisu.log.sdkthrift.service.nft.NFTService;
import com.bamisu.log.sdkthrift.service.vip.VipService;
import com.bamisu.gamelib.base.config.ConfigHandle;
import io.opentelemetry.instrumentation.annotations.WithSpan;
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
public class SDKThriftClient {
    public static TTransport createTransport(String url, int port) {
        return new TFramedTransport(new TSocket(url, port));
    }

    public static TBinaryProtocol createProtocol() throws TTransportException {
        TTransport transport = createTransport(ConfigHandle.instance().get("sdk_t_addr"), ConfigHandle.instance().getInt("sdk_t_port"));
        transport.open();
        return new TBinaryProtocol(transport);
    }

    public static TServiceClient getServiceClient(TProtocol protocol, String serviceName) throws TTransportException {
        TMultiplexedProtocol mp = new TMultiplexedProtocol(protocol, serviceName);
        return getServiceByName(serviceName, mp);
    }

    @WithSpan
    private static TServiceClient getServiceByName(String serviceName, TProtocol protocol) {
        switch (serviceName) {
            case "account":
                return new AccountService.Client(protocol);
            case "giftcode":
                return new GiftcodeService.Client(protocol);
            case "multiserver":
                return new MultiServerService.Client(protocol);
            case "vip":
                return new VipService.Client(protocol);
            case "invite":
                return new InviteService.Client(protocol);
            case "iap":
                return new IAPService.Client(protocol);
            case "nft":
                return new NFTService.Client(protocol);
        }
        return null;
    }
}
