package com.bamisu.log.gameserver.module.ingame.entities.fighting;

import com.bamisu.log.gameserver.module.ingame.FightingExtension;
import com.bamisu.log.gameserver.module.ingame.cmd.rec.RecMove;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.List;

/**
 * Create by Popeye on 5:34 PM, 1/14/2020
 */
public class PvPManager extends FightingManager {
    public PvPManager(Room room) {
        super(room);
        this.type = FightingType.PvP;
    }

    @Override
    public void onPlayerJoin(User user) {

    }

    @Override
    public void onAllPlayerJoin() {

    }

    @Override
    public void startGame(int firstTurn) {

    }

    @Override
    public void move(User user, RecMove recMove) {

    }

    @Override
    public void flee(User user) {

    }

    @Override
    public ISFSObject checkEndGame(boolean isLastTurn) {
        return null;
    }

    @Override
    public List<ActionResult> befoTurn() {
        return null;
    }

    @Override
    public int getCampaignArea() {
        return 0;
    }

    @Override
    public int getCampaignStation() {
        return 0;
    }
}
