package com.bamisu.log.gameserver.gamethriftserver.handler.multiserver;

import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.datamodel.bag.UserBagModel;
import com.bamisu.log.gameserver.manager.ServerManager;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.mail.MailUtils;
import com.bamisu.log.gamethrift.entities.exception.ThriftSVException;
import com.bamisu.gamelib.utils.Utils;
import com.log.bamisu.gamethrift.service.multiserver.MultiserverService;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class TMultiserverHandler implements MultiserverService.Iface {

    @WithSpan
    @Override
    public boolean maintenaceServer(boolean PRE_MAINTENANCE) throws ThriftSVException, TException {
        return ServerManager.getInstance().maintenanceAllServer(PRE_MAINTENANCE);
    }

    @WithSpan
    @Override
    public String getCCUServer() throws ThriftSVException, TException {
        return Utils.toJson(ServerManager.getInstance().getCCU());
    }

    @WithSpan
    @Override
    public boolean activeEventModuleServer(String moduleName, boolean active, int timeStamp, String zoneName) throws ThriftSVException, TException {
        ISFSObject objPut = new SFSObject();
        objPut.putUtfString(Params.MODULE, moduleName);
        objPut.putBool(Params.IS_ACTIVE, active);
        objPut.putInt(Params.TIME, timeStamp);

        if(zoneName == null || zoneName.isEmpty()){
            SmartFoxServer.getInstance().getZoneManager().getZoneList().parallelStream().
                    filter(zone -> !zone.getName().equals("--=={{{ AdminZone }}}==--")).
                    forEach(zone -> zone.getExtension().handleInternalMessage(CMD.InternalMessage.UPDATE_CONFIG_MODULE_SERVER, objPut));
        }else {
            Zone zone = ExtensionUtility.getInstance().getZoneManager().getZoneByName(zoneName);
            if(zone == null) return false;

            zone.getExtension().handleInternalMessage(CMD.InternalMessage.UPDATE_CONFIG_MODULE_SERVER, objPut);
        }

        return true;
    }
}
