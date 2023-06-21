package com.bamisu.log.sdk.module.sdkthriftserver;

import com.bamisu.log.sdk.module.sdkthriftserver.handler.account.TAccountHandler;
import com.bamisu.log.sdk.module.sdkthriftserver.handler.giftcode.TGiftcodeHandler;
import com.bamisu.log.sdk.module.sdkthriftserver.handler.iap.TIAPHandler;
import com.bamisu.log.sdk.module.sdkthriftserver.handler.invitecode.TInviteHandler;
import com.bamisu.log.sdk.module.sdkthriftserver.handler.multiserver.TMultiServerService;
import com.bamisu.log.sdk.module.sdkthriftserver.handler.nft.TNFTHandler;
import com.bamisu.log.sdk.module.sdkthriftserver.handler.vip.TVipHandler;
import com.bamisu.log.sdkthrift.service.account.AccountService;
import com.bamisu.log.sdkthrift.service.giftcode.GiftcodeService;
import com.bamisu.log.sdkthrift.service.iap.IAPService;
import com.bamisu.log.sdkthrift.service.invitecode.InviteService;
import com.bamisu.log.sdkthrift.service.multiserver.MultiServerService;
import com.bamisu.log.sdkthrift.service.nft.NFTService;
import com.bamisu.log.sdkthrift.service.vip.VipService;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.*;

/**
 * Create by Popeye on 9:24 PM, 4/24/2020
 */
public class SDKThriftServer {
    private static SDKThriftServer ourInstance = new SDKThriftServer();
    private Thread thriftServerThread;
    private boolean isStart = false;

    public static SDKThriftServer getInstance() {
        return ourInstance;
    }

    private SDKThriftServer() {
        init();
    }

    private void init() {
        isStart = false;
    }

    public synchronized void start() {
        try {
            if (isStart)
                return;
            TMultiplexedProcessor processor = new TMultiplexedProcessor();
            processor.registerProcessor("account", new AccountService.Processor<>(new TAccountHandler()));
            processor.registerProcessor("giftcode", new GiftcodeService.Processor<>(new TGiftcodeHandler()));
            processor.registerProcessor("multiserver", new MultiServerService.Processor<>(new TMultiServerService()));
            processor.registerProcessor("vip", new VipService.Processor<>(new TVipHandler()));
            processor.registerProcessor("invite", new InviteService.Processor<>(new TInviteHandler()));
            processor.registerProcessor("iap", new IAPService.Processor<>(new TIAPHandler()));
            processor.registerProcessor("nft", new NFTService.Processor<>(new TNFTHandler()));

            TServerTransport serverTransport = new TServerSocket(ThriftUtils.SV_API_PORT);
            TTransportFactory factory = new TFramedTransport.Factory();

            TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);

            args.processor(processor);
            args.transportFactory(factory);

            TThreadPoolServer server = new TThreadPoolServer(args);
            Runnable serve = () -> {
                //System.out.println("\n\n*********************************************************************\n" +
//                        "\n  >> Starting SDK thrift server on port " + ThriftUtils.SV_API_PORT + " ... <<\n\n" +
//                        "*********************************************************************\n\n");
                server.serve();
            };
            if (this.thriftServerThread != null) {
                this.thriftServerThread.interrupt();
            }
            this.thriftServerThread = new Thread(serve);

            this.thriftServerThread.start();
            isStart = true;
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    public synchronized void restart() {
        if (this.thriftServerThread != null) {
            this.thriftServerThread.interrupt();
            isStart = false;
        }

        this.start();
    }
}
