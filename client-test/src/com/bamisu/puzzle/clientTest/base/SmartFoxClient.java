package com.bamisu.puzzle.clientTest.base;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import sfs2x.client.SmartFox;
import sfs2x.client.requests.IRequest;

public class SmartFoxClient extends SmartFox {
    private static volatile LogManager log;

    public SmartFoxClient() {
        super();
        if(log == null){
            log = new LogManager();
            log.start();
        }
    }

    public SmartFoxClient(boolean debug) {
        super(debug);
        if(log == null){
            log = new LogManager();
            log.start();
        }
    }

    @Override
    public void send(IRequest request) {
        super.send(request);
        log.onExtensionRequest(request);
    }

    public void onResponse(ISFSObject reponse){
        log.onExtensionResponse(reponse);
    }
}
