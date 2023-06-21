package com.bamisu.log.gameserver.module.ingame;

import com.bamisu.gamelib.base.BaseExtension;
import com.bamisu.log.gameserver.module.ingame.cmd.rec.*;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingFactory;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingManager;
import com.bamisu.gamelib.base.ExtensionBaseClientRequestHandler;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Create by Popeye on 4:42 PM, 2/6/2020
 */
public class FightingHandler extends ExtensionBaseClientRequestHandler {
//    FightingManager fightingManager;
//    Room room;
    private static FightingHandler instance;

    public FightingHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.FIGHTING;
        instance = this;
//        room = extension.getParentRoom();
//        fightingManager = extension.getFightingManager();
    }

    public static FightingHandler getInstance() {
        return instance;
    }



    @WithSpan
    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId){
            case CMD.CMD_FIGHTING_MOVE:
                handleMove(user, data);
                break;
            case CMD.CMD_FIGHTING_FLEE:
                handleFlee(user, data);
                break;
            case CMD.CMD_SAGE_SKILL:
                handleSageSkilling(user, data);
                break;
            case CMD.CMD_HERO_SKILL:
                handleHeroSkilling(user, data);
                break;
            case CMD.CMD_CELESTIAL_SKILL:
                handleCelestialSkilling(user, data);
                break;
            case CMD.CMD_UPDATE_PUZZLE_BOARD:
                handleUpdatePuzzleBoard(user, data);
                break;
            case CMD.CMD_SELECT_TARGET:
                handleSelectTarget(user, data);
                break;
            case CMD.CMD_START_MISSION:
                handleStartMission(user, data);
                break;
        }
    }

    @WithSpan
    private void handleStartMission(User user, ISFSObject data) {
        FightingManager fightingManager = this.getFightingManager(user);
        if (fightingManager == null) {
            return;
        }
        fightingManager.startMisson();
    }

    @WithSpan
    private void handleSelectTarget(User user, ISFSObject data) {
        RecSelectTarget rec = new RecSelectTarget(data);
        FightingManager fightingManager = this.getFightingManager(user);
        if (fightingManager == null) {
            return;
        }

        fightingManager.selectTarget(user, rec.actorID);
    }

    @WithSpan
    private void handleUpdatePuzzleBoard(User user, ISFSObject data) {
        RecUpdatePuzzleBoard rec = new RecUpdatePuzzleBoard(data);
    }

    @WithSpan
    private void handleCelestialSkilling(User user, ISFSObject data) {
        FightingManager fightingManager = this.getFightingManager(user);
        if (fightingManager == null) {
            return;
        }

        fightingManager.celestialUltilmate(user);
    }

    @WithSpan
    private void handleHeroSkilling(User user, ISFSObject data) {
        RecHeroSkill rec = new RecHeroSkill(data);
        FightingManager fightingManager = this.getFightingManager(user);
        if (fightingManager == null) {
            return;
        }

        fightingManager.heroUltilmate(user, rec.actorID);
    }

    @WithSpan
    private void handleSageSkilling(User user, ISFSObject data) {
        RecSageSkill rec = new RecSageSkill(data);

        //test
//        fightingManager.celestialUltilmate(user);
        FightingManager fightingManager = this.getFightingManager(user);
        if (fightingManager == null) {
            return;
        }

        fightingManager.sageSkilling(user, rec.skillID);
    }

    @WithSpan
    private void handleFlee(User user, ISFSObject data) {
        FightingManager fightingManager = this.getFightingManager(user);
        if (fightingManager == null) {
            return;
        }

        fightingManager.flee(user);
    }

    @WithSpan
    private void handleMove(User user, ISFSObject data) {
        RecMove recMove = new RecMove(data);
        recMove.unpackData();
        FightingManager fightingManager = this.getFightingManager(user);
        if (fightingManager == null) {
            return;
        }
//        if(recMove.to != null){
            fightingManager.move(user, recMove);
//        }else {
//            fightingManager.touch(user, recMove);
//        }

    }

    @WithSpan
    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {
        switch (type) {
            case USER_JOIN_ROOM:
                handleJoinRoom(event);
                break;
//                handleDisConnect(event);
//                break;
            case USER_LEAVE_ROOM:
                handleLeaveRoom(event);
                break;
            case USER_DISCONNECT:
            case USER_LOGOUT:
                handleDisConnect(event);
                break;
            default:
                extendServerEvent(type, event);
                break;
        }
    }

    private void extendServerEvent(SFSEventType type, ISFSEvent event) {

    }

    private void handleLeaveRoom(ISFSEvent event) {
        User user = (User) event.getParameter(SFSEventParam.USER);
        Room room = user.getLastJoinedRoom();
        if (room != null && room.isGame()) {
            System.out.println("user " + user.getName() + " leave room " + room.getName());
        } else {
            System.out.println("Room is null");
        }
    }

    @WithSpan
    private void handleDisConnect(ISFSEvent event) {
        User user = (User) event.getParameter(SFSEventParam.USER);
        FightingManager fightingManager = this.getFightingManager(user);
        if (fightingManager == null) {
            return;
        }
        fightingManager.flee(user);
    }

    @WithSpan
    private void handleJoinRoom(ISFSEvent event) {
        User user = (User) event.getParameter(SFSEventParam.USER);
        Room room = user.getLastJoinedRoom();
        if (room.getVariable(Params.FUNCTION) == null) {
            return;
        }

        FightingManager fightingManager = FightingFactory.newFightingManager(room);

        if (fightingManager == null) {
            return;
        }
        room.setProperty("manager", fightingManager);
        fightingManager.onPlayerJoin(user);
    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.FIGHTING, this);
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addEventHandler(SFSEventType.USER_JOIN_ROOM, this);
        this.extension.addEventHandler(SFSEventType.USER_DISCONNECT, this);
        this.extension.addEventHandler(SFSEventType.USER_LEAVE_ROOM, this);

        this.extension.addServerHandler(Params.Module.FIGHTING, this);
    }

    @WithSpan
    public final void sendToAnother(BaseMsg cmd, User user) {
        Room room = user.getLastJoinedRoom();
        List<User> userList = room.getUserList();
        userList.remove(user);
        send(cmd, userList);
    }

    @WithSpan
    public final void broadcast(BaseMsg cmd, Room room) {
        send(cmd, room.getUserList());
    }
    public final void broadcast(BaseMsg cmd) {
//        send(cmd, room.getUserList());
    }

    private FightingManager getFightingManager(User user) {
        Room room = user.getLastJoinedRoom();
        if (room != null) {
            return (FightingManager) room.getProperty("manager");
        }

        return null;
    }
}
