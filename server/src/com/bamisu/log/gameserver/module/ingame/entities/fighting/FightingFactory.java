package com.bamisu.log.gameserver.module.ingame.entities.fighting;

import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.Room;

/**
 * Create by Popeye on 5:58 PM, 1/14/2020
 */
public class FightingFactory {

    public static FightingManager newFightingManager(Room room) {
        switch (FightingType.fromType(room.getVariable(Params.TYPE).getIntValue())) {
            case PvM:
                if (room.getVariable(Params.FUNCTION).getIntValue() == EFightingFunction.CAMPAIGN.getIntValue()) {
                    return new CampaignFightManager(room);
                }
                if (room.getVariable(Params.FUNCTION).getIntValue() == EFightingFunction.MISSION.getIntValue()) {
                    return new MissionFightingManager(room);
                }
                if (room.getVariable(Params.FUNCTION).getIntValue() == EFightingFunction.HUNT.getIntValue()) {
                    return new HuntFightingManager(room);
                }
                if (room.getVariable(Params.FUNCTION).getIntValue() == EFightingFunction.TOWER.getIntValue()) {
                    return new TowerFightingManager(room);
                }
                if (room.getVariable(Params.FUNCTION).getIntValue() == EFightingFunction.PvP_FRIEND.getIntValue()) {
                    return new PvPOfflineFightingManager(room);
                }
                if (room.getVariable(Params.FUNCTION).getIntValue() == EFightingFunction.PvP_ARENA.getIntValue()) {
                    return new PvPOfflineArenaManager(room);
                }
                if (room.getVariable(Params.FUNCTION).getIntValue() == EFightingFunction.DARK_REALM.getIntValue()) {
                    return new DarkRealmFightingManager(room);
                }
                if (room.getVariable(Params.FUNCTION).getIntValue() == EFightingFunction.ENDLESS_NIGHT.getIntValue()) {
                    return new EndlessNightFightingManager(room);
                }
            case PvP:
                return new PvPManager(room);
            case PvB:
                return new PvBManager(room);
            default:
                return null;
        }
    }
}
