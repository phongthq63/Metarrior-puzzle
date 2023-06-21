package com.bamisu.gamelib.utils.extension;


import com.bamisu.gamelib.base.event.InternalMessage;
import com.bamisu.gamelib.base.event.exception.ExceptionInternalMessage;
import com.bamisu.gamelib.utils.management.interface_.IExtensionZoneManager;
import com.bamisu.gamelib.base.event.InternalMessage;
import com.bamisu.gamelib.base.event.exception.ExceptionInternalMessage;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.utils.management.interface_.IExtensionZoneManager;
import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.managers.IExtensionManager;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;
import com.smartfoxserver.v2.exceptions.SFSJoinRoomException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.ISFSExtension;
import com.smartfoxserver.v2.util.IDisconnectionReason;
import com.smartfoxserver.v2.util.TaskScheduler;

/**
 * Created by Popeye on 6/21/2017.
 */
public interface IExtensionUtility {

    void logout(User user);

    void disconnect(ISession session);

    void disconnectUser(User user);

    void disconnectUser(User user, IDisconnectionReason reason);

    void kickUser(User userToKick, User modUser, String kickMessage, int delaySeconds);

    User getUserById(long userId);

    User getUserById(String userId);

    User getUserBySession(ISession session);

    Room createRoom(Zone zone, CreateRoomSettings settings) throws SFSCreateRoomException;

    Room createRoom(Zone zone, CreateRoomSettings settings, User owner) throws SFSCreateRoomException;

    Room createRoom(Zone zone, CreateRoomSettings settings, User owner, boolean joinIt, Room roomToLeave) throws SFSCreateRoomException;

    Room createRoom(Zone zone, CreateRoomSettings settings, User owner, boolean joinIt, Room roomToLeave, boolean fireClientEvent, boolean fireServerEvent) throws SFSCreateRoomException;

    void joinRoom(User user, Room room) throws SFSJoinRoomException;

    void joinRoom(User user, Room roomToJoin, String password, boolean asSpectator, Room roomToLeave) throws SFSJoinRoomException;

    void joinRoom(User user, Room roomToJoin, String password, boolean asSpectator, Room roomToLeave, boolean fireClientEvent, boolean fireServerEvent) throws SFSJoinRoomException;

    void leaveRoom(User user, Room room);

    void leaveRoom(User user, Room room, boolean fireClientEvent, boolean fireServerEvent);

    void removeRoom(Room room);

    void removeRoom(Room room, boolean fireClientEvent, boolean fireServerEvent);

    IExtensionZoneManager getZoneManager();

    IExtensionManager getExtensionManager();

    ISFSExtension getZoneExtension(String zoneName);

    ISFSObject handlerInternalMessage(Zone zone, InternalMessage message) throws ExceptionInternalMessage;
}
