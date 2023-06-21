package com.bamisu.log.gameserver.gamethriftserver;

import com.bamisu.log.gameserver.gamethriftserver.handler.IAP.TIAPClientHandler;
import com.bamisu.log.gameserver.gamethriftserver.handler.arena.TArenaHandler;
import com.bamisu.log.gameserver.gamethriftserver.handler.mail.TMailHandler;
import com.bamisu.log.gameserver.gamethriftserver.handler.multiserver.TMultiserverHandler;
import com.bamisu.log.gameserver.gamethriftserver.handler.event.TEventHandler;
import com.bamisu.gamelib.base.config.ConfigHandle;
import com.log.bamisu.gamethrift.service.IAP.IAPClientService;
import com.log.bamisu.gamethrift.service.arena.ArenaService;
import com.log.bamisu.gamethrift.service.event.EventService;
import com.log.bamisu.gamethrift.service.mail.MailService;
import com.log.bamisu.gamethrift.service.multiserver.MultiserverService;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.*;

/**
 * Created by Popeye on 11/10/2017.
 */
public class ThriftServer {

    private static ThriftServer ourInstance = new ThriftServer();
    private Thread thriftServerThread;
    private boolean isStart = false;
    private int gameThriftPort = ConfigHandle.instance().getInt("game_t_port");

    public static ThriftServer getInstance() {
        return ourInstance;
    }

    private ThriftServer() {
        init();
    }

    private void init() {
        isStart = false;
    }

    public synchronized void start() {
        try {
            if (isStart)
                return;

            System.out.println("\n\n\nSTART THRIFT\n\n\n");
            TMultiplexedProcessor processor = new TMultiplexedProcessor();

            processor.registerProcessor("multiserver", new MultiserverService.Processor<>(new TMultiserverHandler()));
            processor.registerProcessor("mail", new MailService.Processor<>(new TMailHandler()));
            processor.registerProcessor("iap", new IAPClientService.Processor<>(new TIAPClientHandler()));
            processor.registerProcessor("event", new EventService.Processor<>(new TEventHandler()));
            processor.registerProcessor("arena", new ArenaService.Processor<>(new TArenaHandler()));


            TServerTransport serverTransport = new TServerSocket(gameThriftPort);
            TTransportFactory factory = new TFramedTransport.Factory();

            TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);

            args.processor(processor);
            args.transportFactory(factory);

            TThreadPoolServer server = new TThreadPoolServer(args);
            Runnable serve = () -> {
                //System.out.println("\n\n*********************************************************************\n" +
//                        "\n  >> Starting thrift server on port " + gameThriftPort + " ... <<\n\n" +
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
//            e.printStackTrace();
//            Logger.getLogger("error").info("=== khởi tạo thrift port ===");
            return;
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
