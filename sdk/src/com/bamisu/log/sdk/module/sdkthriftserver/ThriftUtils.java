package com.bamisu.log.sdk.module.sdkthriftserver;

import com.bamisu.log.sdk.base.SDKConfigHandle;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by Popeye on 11/10/2017.
 */
public class ThriftUtils {

    public static final int SV_API_PORT = SDKConfigHandle.instance().getInt("t_port");
    public static TTransport createTransport() {
        return new TSocket("localhost", SV_API_PORT);
    }

    public static TBinaryProtocol createProtocol() throws TTransportException {
        TTransport transport = createTransport();
        transport.open();
        return new TBinaryProtocol(transport);
    }
}
