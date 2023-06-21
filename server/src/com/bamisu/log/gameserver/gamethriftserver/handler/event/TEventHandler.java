package com.bamisu.log.gameserver.gamethriftserver.handler.event;

import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.module.event.EventInGameManager;
import com.bamisu.log.gameserver.module.notification.NotificationManager;
import com.bamisu.log.gameserver.module.notification.defind.EActionNotiModel;
import com.bamisu.log.gameserver.module.notification.entities.InfoEventNoti;
import com.bamisu.log.gamethrift.entities.exception.ThriftSVException;
import com.log.bamisu.gamethrift.service.event.EventService;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.SFSObject;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TEventHandler implements EventService.Iface {

    @WithSpan
    @Override
    public boolean addEvent(String zoneName, String jsonData) throws ThriftSVException, TException {
        InfoEventNoti info = Utils.fromJson(jsonData, InfoEventNoti.class);

        SFSObject objPut = new SFSObject();
        objPut.putUtfStringArray(Params.ID, Collections.singletonList(info.id));
        objPut.putUtfString(Params.ACTION, EActionNotiModel.SHOW.getId());

        if(zoneName == null || zoneName.isEmpty()){
            SmartFoxServer.getInstance().getZoneManager().getZoneList().stream().
                    filter(zone -> !zone.getName().equals("--=={{{ AdminZone }}}==--")).
                    forEach(zone -> {
                        EventInGameManager.getInstance().addEventGeneral(info, zone);
                        zone.getExtension().handleInternalMessage(CMD.InternalMessage.SEND_ALL_PLAYER_NOTIFY_MODEL, objPut);
                    });
        }else {
            Zone zone = ExtensionUtility.getInstance().getZoneManager().getZoneByName(zoneName);
            if(zone == null) return false;

            EventInGameManager.getInstance().addEventGeneral(info, zone);
            zone.getExtension().handleInternalMessage(CMD.InternalMessage.SEND_ALL_PLAYER_NOTIFY_MODEL, objPut);
        }

        return true;
    }

    @WithSpan
    @Override
    public boolean removeEvent(String zoneName, List<String> listId) throws ThriftSVException, TException {
        SFSObject objPut = new SFSObject();
        objPut.putUtfStringArray(Params.ID, listId);
        objPut.putUtfString(Params.ACTION, EActionNotiModel.REMOVE.getId());

        if(zoneName == null || zoneName.isEmpty()){
            SmartFoxServer.getInstance().getZoneManager().getZoneList().stream().
                    filter(zone -> !zone.getName().equals("--=={{{ AdminZone }}}==--")).
                    forEach(zone -> {
                        EventInGameManager.getInstance().removeEventGeneral(listId, zone);
                        zone.getExtension().handleInternalMessage(CMD.InternalMessage.SEND_ALL_PLAYER_NOTIFY_MODEL, objPut);
                    });
        }else {
            Zone zone = ExtensionUtility.getInstance().getZoneManager().getZoneByName(zoneName);
            if(zone == null) return false;

            EventInGameManager.getInstance().removeEventGeneral(listId, ExtensionUtility.getInstance().getZoneManager().getZoneByName(zoneName));
            zone.getExtension().handleInternalMessage(CMD.InternalMessage.SEND_ALL_PLAYER_NOTIFY_MODEL, objPut);
        }

        return true;
    }

    @WithSpan
    @Override
    public boolean addSpecialEvent(String zoneName, String jsonData) throws ThriftSVException, TException {
        InfoEventNoti info = Utils.fromJson(jsonData, InfoEventNoti.class);

        SFSObject objPut = new SFSObject();
        objPut.putUtfStringArray(Params.ID, Collections.singletonList(info.id));
        objPut.putUtfString(Params.ACTION, EActionNotiModel.SHOW.getId());

        if(zoneName == null || zoneName.isEmpty()){
            SmartFoxServer.getInstance().getZoneManager().getZoneList().stream().
                    filter(zone -> !zone.getName().equals("--=={{{ AdminZone }}}==--")).
                    forEach(zone -> {
                        EventInGameManager.getInstance().addEventSpecial(info, zone);
                        zone.getExtension().handleInternalMessage(CMD.InternalMessage.SEND_ALL_PLAYER_NOTIFY_MODEL, objPut);
                    });
        }else {
            Zone zone = ExtensionUtility.getInstance().getZoneManager().getZoneByName(zoneName);
            if(zone == null) return false;

            EventInGameManager.getInstance().addEventSpecial(info, ExtensionUtility.getInstance().getZoneManager().getZoneByName(zoneName));
            zone.getExtension().handleInternalMessage(CMD.InternalMessage.SEND_ALL_PLAYER_NOTIFY_MODEL, objPut);
        }

        return true;
    }

    @WithSpan
    @Override
    public boolean removeSpecialEvent(String zoneName, List<String> listId) throws ThriftSVException, TException {
        SFSObject objPut = new SFSObject();
        objPut.putUtfStringArray(Params.ID, listId);
        objPut.putUtfString(Params.ACTION, EActionNotiModel.REMOVE.getId());

        if(zoneName == null || zoneName.isEmpty()){
            SmartFoxServer.getInstance().getZoneManager().getZoneList().stream().
                    filter(zone -> !zone.getName().equals("--=={{{ AdminZone }}}==--")).
                    forEach(zone -> {
                        EventInGameManager.getInstance().removeEventSpecial(listId, zone);
                        zone.getExtension().handleInternalMessage(CMD.InternalMessage.SEND_ALL_PLAYER_NOTIFY_MODEL, objPut);
                    });
        }else {
            Zone zone = ExtensionUtility.getInstance().getZoneManager().getZoneByName(zoneName);
            if(zone == null) return false;

            EventInGameManager.getInstance().removeEventSpecial(listId, ExtensionUtility.getInstance().getZoneManager().getZoneByName(zoneName));
            zone.getExtension().handleInternalMessage(CMD.InternalMessage.SEND_ALL_PLAYER_NOTIFY_MODEL, objPut);
        }

        return true;
    }
}
