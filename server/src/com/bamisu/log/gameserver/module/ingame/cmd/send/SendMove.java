package com.bamisu.log.gameserver.module.ingame.cmd.send;

import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingManager;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.TeamSlot;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.SFSArray;
import org.apache.xmlbeans.impl.xb.xsdschema.All;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Create by Popeye on 4:41 PM, 2/10/2020
 */
public class SendMove extends BaseMsg {
    ISFSArray packageList = new SFSArray();
    private boolean isEndGame = false;
    public FightingManager fightingManager;
    public Collection<String> willFight = new ArrayList<>();  //báo trước enemy sẽ đánh

    public SendMove() {
        super(CMD.CMD_FIGHTING_MOVE);
    }

    public SendMove(short errorCode) {
        super(CMD.CMD_FIGHTING_MOVE, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) return;
        data.putSFSArray(Params.MOVE_LIST, packageList);
        data.putUtfStringArray(Params.WILL_FIGHT, willFight);
//        System.out.println("move: " + data.toJson());
    }

    public void pushPackage(MovePackage movePackage) {
        packageList.addSFSObject(movePackage.data);
    }

    public boolean isEndGame() {
        return isEndGame;
    }

    public void setEndGame(boolean endGame) {
        isEndGame = endGame;
    }
}
