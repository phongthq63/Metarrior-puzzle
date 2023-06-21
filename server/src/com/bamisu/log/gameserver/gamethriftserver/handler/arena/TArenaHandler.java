package com.bamisu.log.gameserver.gamethriftserver.handler.arena;

import com.bamisu.gamelib.entities.CMD;
import com.bamisu.log.gamethrift.entities.exception.ThriftSVException;
import com.log.bamisu.gamethrift.service.arena.ArenaService;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.Zone;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.thrift.TException;

public class TArenaHandler implements ArenaService.Iface {

    @WithSpan
    @Override
    public boolean sendGiftArenaDaily(String zoneName) throws ThriftSVException, TException {
        if(zoneName == null || zoneName.isEmpty()){
            SmartFoxServer.getInstance().getZoneManager().getZoneList().stream().
                    filter(zone -> !zone.getName().equals("--=={{{ AdminZone }}}==--")).
                    forEach(zone -> {
                        zone.getExtension().handleInternalMessage(CMD.InternalMessage.ARENA_END_DAY, null);
                    });
        }else {
            Zone zone = SmartFoxServer.getInstance().getZoneManager().getZoneByName(zoneName);
            if(zone == null) return false;
            zone.getExtension().handleInternalMessage(CMD.InternalMessage.ARENA_END_DAY, null);
        }

        return true;
    }

    @WithSpan
    @Override
    public boolean closeSeasonArena(String zoneName) throws ThriftSVException, TException {
        if(zoneName == null || zoneName.isEmpty()){
            SmartFoxServer.getInstance().getZoneManager().getZoneList().stream().
                    filter(zone -> !zone.getName().equals("--=={{{ AdminZone }}}==--")).
                    forEach(zone -> {
                        zone.getExtension().handleInternalMessage(CMD.InternalMessage.CLOSE_SEASON_ARENA, null);
                    });
        }else {
            Zone zone = SmartFoxServer.getInstance().getZoneManager().getZoneByName(zoneName);
            if(zone == null) return false;
            zone.getExtension().handleInternalMessage(CMD.InternalMessage.CLOSE_SEASON_ARENA, null);
        }

        return true;
    }

    @WithSpan
    @Override
    public boolean openSeasonArena(String zoneName) throws ThriftSVException, TException {
        if(zoneName == null || zoneName.isEmpty()){
            SmartFoxServer.getInstance().getZoneManager().getZoneList().stream().
                    filter(zone -> !zone.getName().equals("--=={{{ AdminZone }}}==--")).
                    forEach(zone -> {
                        zone.getExtension().handleInternalMessage(CMD.InternalMessage.OPEN_SEASON_ARENA, null);
                    });
        }else {
            Zone zone = SmartFoxServer.getInstance().getZoneManager().getZoneByName(zoneName);
            if(zone == null) return false;
            zone.getExtension().handleInternalMessage(CMD.InternalMessage.OPEN_SEASON_ARENA, null);
        }

        return true;
    }
}
