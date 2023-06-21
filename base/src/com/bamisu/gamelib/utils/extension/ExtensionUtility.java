package com.bamisu.gamelib.utils.extension;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.datacontroller.ZoneDatacontroler;
import com.bamisu.gamelib.base.event.InternalMessage;
import com.bamisu.gamelib.base.event.exception.ExceptionInternalMessage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.manager.UserManager;
import com.bamisu.gamelib.utils.management.ExtensionZoneManager;
import com.bamisu.gamelib.utils.management.interface_.IExtensionZoneManager;
import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.gamelib.base.datacontroller.ZoneDatacontroler;
import com.bamisu.gamelib.base.event.InternalMessage;
import com.bamisu.gamelib.base.event.exception.ExceptionInternalMessage;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.manager.UserManager;
import com.bamisu.gamelib.utils.management.ExtensionZoneManager;
import com.bamisu.gamelib.utils.management.interface_.IExtensionZoneManager;
import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.CreateRoomSettings;
import com.smartfoxserver.v2.api.ISFSApi;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.managers.IExtensionManager;
import com.smartfoxserver.v2.exceptions.SFSCreateRoomException;
import com.smartfoxserver.v2.exceptions.SFSJoinRoomException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.ISFSExtension;
import com.smartfoxserver.v2.extensions.SFSExtension;
import com.smartfoxserver.v2.util.IDisconnectionReason;
import com.smartfoxserver.v2.util.TaskScheduler;

/**
 * Created by Popeye on 6/21/2017.
 */
public class ExtensionUtility implements IExtensionUtility {
    private static ExtensionUtility ourInstance = new ExtensionUtility();

    public static ExtensionUtility getInstance() {
        return ourInstance;
    }

    private SmartFoxServer sfs;
    private ISFSApi sfsApi;
    private IExtensionZoneManager zoneManager;
    private IExtensionManager extensionManager;

    private ExtensionUtility() {
        sfs = SmartFoxServer.getInstance();
        sfsApi = sfs.getAPIManager().getSFSApi();
        zoneManager = ExtensionZoneManager.getInstance();
        extensionManager = sfs.getExtensionManager();
    }

    @Override
    public void logout(User user) {
        sfsApi.logout(user);
    }

    @Override
    public void disconnect(ISession session) {
        sfsApi.disconnect(session);
    }

    @Override
    public void disconnectUser(User user) {
        sfsApi.disconnectUser(user);
    }

    @Override
    public void disconnectUser(User user, IDisconnectionReason reason) {
        sfsApi.disconnectUser(user, reason);
    }

    @Override
    public void kickUser(User userToKick, User modUser, String kickMessage, int delaySeconds) {
        sfsApi.kickUser(userToKick, modUser, kickMessage, delaySeconds);
    }

    @Override
    public User getUserById(long userId) {
        return sfsApi.getUserByName(String.valueOf(userId));
    }


    @Override
    public User getUserById(String userId) {
        return sfsApi.getUserByName(userId);
    }


    @Override
    public User getUserBySession(ISession session) {
        return sfsApi.getUserBySession(session);
    }

    @Override
    /**
     * Zone zone, CreateRoomSettings settings, User owner
     */
    public Room createRoom(Zone zone, CreateRoomSettings settings) throws SFSCreateRoomException {
        return sfsApi.createRoom(zone, settings, null);
    }

    @Override
    /**
     * Zone zone, CreateRoomSettings settings, User owner
     */
    public Room createRoom(Zone zone, CreateRoomSettings settings, User owner) throws SFSCreateRoomException {
        return this.createRoom(zone, settings, owner, true, null);
    }

    @Override
    /**
     * Zone zone, CreateRoomSettings settings, User owner, boolean joinIt, Room roomToLeave
     */
    public Room createRoom(Zone zone, CreateRoomSettings settings, User owner, boolean joinIt, Room roomToLeave) throws SFSCreateRoomException {
        return this.createRoom(zone, settings, owner, true, null, true, true);
    }

    @Override
    /**
     * Zone zone, CreateRoomSettings settings, User owner, boolean joinIt, Room roomToLeave, boolean fireClientEvent, boolean fireServerEvent
     */
    public Room createRoom(Zone zone, CreateRoomSettings settings, User owner, boolean joinIt, Room roomToLeave, boolean fireClientEvent, boolean fireServerEvent) throws SFSCreateRoomException {
        return sfsApi.createRoom(zone, settings, owner, joinIt, roomToLeave, fireClientEvent, fireServerEvent);
    }

    @Override
    /**
     * User user, Room room
     */
    public void joinRoom(User user, Room room) throws SFSJoinRoomException {
        this.joinRoom(user, room, null, false, null);
    }

    @Override
    /**
     * User user, Room roomToJoin, String password, boolean asSpectator, Room roomToLeave
     */
    public void joinRoom(User user, Room roomToJoin, String password, boolean asSpectator, Room roomToLeave) throws SFSJoinRoomException {
        this.joinRoom(user, roomToJoin, password, asSpectator, roomToLeave, true, true);
    }

    @Override
    /**
     * User user, Room roomToJoin, String password, boolean asSpectator, Room roomToLeave, boolean fireClientEvent, boolean fireServerEvent
     */
    public void joinRoom(User user, Room roomToJoin, String password, boolean asSpectator, Room roomToLeave, boolean fireClientEvent, boolean fireServerEvent) throws SFSJoinRoomException {
        sfsApi.joinRoom(user, roomToJoin, password, asSpectator, roomToLeave, fireClientEvent, fireServerEvent);
    }

    @Override
    /**
     * User user, Room room
     */
    public void leaveRoom(User user, Room room) {
        this.leaveRoom(user, room, true, true);
    }

    @Override
    /**
     * User user, Room room, boolean fireClientEvent, boolean fireServerEvent
     */
    public void leaveRoom(User user, Room room, boolean fireClientEvent, boolean fireServerEvent) {
        sfsApi.leaveRoom(user, room, fireClientEvent, fireServerEvent);
    }

    @Override
    /**
     * Room room
     */
    public void removeRoom(Room room) {
        this.removeRoom(room, true, true);
    }

    @Override
    /**
     * Room room, boolean fireClientEvent, boolean fireServerEvent
     */
    public void removeRoom(Room room, boolean fireClientEvent, boolean fireServerEvent) {
        sfsApi.removeRoom(room, fireClientEvent, fireServerEvent);
    }

    @Override
    public IExtensionZoneManager getZoneManager() {
        return zoneManager;
    }

    @Override
    public IExtensionManager getExtensionManager() {
        return extensionManager;
    }

    @Override
    public ISFSExtension getZoneExtension(String zoneName) {
        Zone zone = zoneManager.getZoneByName(zoneName);
        return extensionManager.getZoneExtension(zone);
    }

    @Override
    public ISFSObject handlerInternalMessage(Zone zone, InternalMessage message) throws ExceptionInternalMessage {
        ISFSExtension extension = zone.getExtension();
        if (extension == null || !(extension instanceof BaseExtension)) {
            throw new ExceptionInternalMessage(ServerConstant.ErrorCode.ERR_BAD_EXTENSION, "bad extension!");
        }
        return ((BaseExtension) extension).handleInternalMessage(message);
    }

    public ZoneDatacontroler getZoneDatacontroller(Zone zone) {
        return ((BaseExtension) zone).getDataController();
    }

    public UserManager getUserManager(Zone zone) {
        return ((BaseExtension) zone.getExtension()).getUserManager();
    }

    public UserManager getUserManager(SFSExtension sfsExtension) {
        return ((BaseExtension) sfsExtension).getUserManager();
    }
}
