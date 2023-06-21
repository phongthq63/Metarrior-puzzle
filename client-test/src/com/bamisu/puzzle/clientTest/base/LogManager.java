package com.bamisu.puzzle.clientTest.base;

import com.bamisu.puzzle.clientTest.Client;
import com.bamisu.puzzle.clientTest.test.PerformanceTest;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import sfs2x.client.requests.IRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LogManager {
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    private PerformanceTest test = new PerformanceTest();


    public void start(){
        log();
    }

    private void log(){
        test.show();

        scheduler.schedule(this::log, 10, TimeUnit.SECONDS);
    }

    public void onExtensionRequest(IRequest request){
        test.onExtensionRequest();
    }

    public void onExtensionResponse(ISFSObject reponse){
        test.onExtensionResponse();
    }
}
