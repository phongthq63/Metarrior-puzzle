package com.bamisu.log.gameserver.module.ingame;

import com.bamisu.log.gameserver.entities.ExtensionClass;
import com.bamisu.gamelib.utils.business.Debug;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.SFSRoom;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;

/**
 * Create by Popeye on 3:43 PM, 5/11/2020
 */
public class FightingCreater {
    public static void creatorFightingRoom(Zone zone, User user, CreateRoomSettings cfg) throws SFSCreateRoomException {
//        Debug.info("JoinedRooms: " + user.getJoinedRooms().size());
//        for(Room room : user.getJoinedRooms()){
//            if(room.isGame()){
//                Debug.info("REMOVE GAME ROOM");
//                ExtensionUtility.getInstance().removeRoom(room);
//            }
//        }
//        cfg.setExtension(new CreateRoomSettings.RoomExtensionSettings(zone.getExtension().getName(), ExtensionClass.FIGHTING_EXT));
        cfg.setGame(true);
        cfg.setDynamic(true);
        cfg.setGroupId("game");
        Room room = ExtensionUtility.getInstance().createRoom(zone, cfg, user, false, null);
        System.out.println("room created: " + room.getName());
    }
}
