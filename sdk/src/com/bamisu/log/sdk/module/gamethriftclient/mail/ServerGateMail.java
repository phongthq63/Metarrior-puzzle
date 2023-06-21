package com.bamisu.log.sdk.module.gamethriftclient.mail;

import com.bamisu.log.sdk.module.gamethriftclient.GameServerThriftClient;
import com.bamisu.log.sdk.module.gamethriftclient.mail.entities.MailInfo;
import com.log.bamisu.gamethrift.service.mail.MailService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import java.util.List;

public class ServerGateMail {
    public static final String serviceName = "mail";

    public static String sendMailAllPlayer(String addr, int port, String zoneName, List<Long> listUid, MailInfo mail) throws TException {
        String result = "";
        TProtocol protocol = null;
        try {
            protocol = GameServerThriftClient.createProtocol(addr, port);
            MailService.Client client = (MailService.Client) GameServerThriftClient.getServiceClient(protocol, serviceName);
            result =  client.sendToPlayer(zoneName, listUid, mail.title, mail.content, mail.gift);
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
