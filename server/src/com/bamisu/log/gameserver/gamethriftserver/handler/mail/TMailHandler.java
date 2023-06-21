package com.bamisu.log.gameserver.gamethriftserver.handler.mail;

import com.bamisu.log.gameserver.module.mail.MailUtils;
import com.bamisu.log.gamethrift.entities.exception.ThriftSVException;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.log.bamisu.gamethrift.service.mail.MailService;
import com.smartfoxserver.v2.SmartFoxServer;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.thrift.TException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TMailHandler implements MailService.Iface {

    @WithSpan
    @Override
    public String sendToPlayer(String zoneName, List<Long> uids, String title, String content, String gift) throws ThriftSVException, TException {
        List<ResourcePackage> resource = Utils.fromJsonList(gift, ResourcePackage.class);

        String result = "";
        if(zoneName == null || zoneName.isEmpty()){
            Map<String,String> mapResult = new HashMap<>();

            SmartFoxServer.getInstance().getZoneManager().getZoneList().stream().
                    filter(zone -> !zone.getName().equals("--=={{{ AdminZone }}}==--")).

                    forEach(zone -> mapResult.put(zone.getName(), MailUtils.getInstance().sendMailToPlayer(zone, uids, resource, title, content)));

            for(String key : mapResult.keySet()){
                if(mapResult.get(key).isEmpty()){
                    result += key + ": success\n";
                }else {
                    result += key + ": " + mapResult.get(key) + "\n";
                }
            }
        }else {
            result = MailUtils.getInstance().sendMailToPlayer(ExtensionUtility.getInstance().getZoneManager().getZoneByName(zoneName), uids, resource, title, content);
        }

        return result;
    }
}
