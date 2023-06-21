package com.bamisu.log.gameserver.module.ingame.entities.fighting;

import com.bamisu.log.gameserver.module.characters.element.Element;
import com.bamisu.log.gameserver.module.ingame.*;
import com.bamisu.log.gameserver.module.ingame.cmd.rec.RecMove;
import com.bamisu.log.gameserver.module.ingame.cmd.send.*;
import com.bamisu.log.gameserver.module.ingame.entities.BaseTable;
import com.bamisu.log.gameserver.module.ingame.entities.Diamond;
import com.bamisu.log.gameserver.module.ingame.entities.MatchState;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.actor.ActorStatistical;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action.SkillingAction;
import com.bamisu.log.gameserver.module.ingame.entities.player.*;
import com.bamisu.log.gameserver.module.skill.Skill;
import com.bamisu.gamelib.entities.LIZRandom;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.RandomObj;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.task.LizThreadManager;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Create by Popeye on 5:33 PM, 1/14/2020
 */
public abstract class FightingManager {
    private final Room room;
    public String battleID = Utils.genMatchID();
//    public FightingExtension fightingExtension;
    public List<BasePlayer> players = new ArrayList<>();
    private int currentTurn; //xác định lượt đánh hiện tại là của player nào
    private MatchState state;
    public FightingType type;
    public EFightingFunction function;
    public List<ResourcePackage> reward = new ArrayList<>();
    public PuzzleTable puzzleTable = new PuzzleTable();
    public int actorIDCounter = 0;
    public int turnCount = 1;
    public int maxTurn = 20;
    public Element mapBonus;
    public boolean bossMode = false;
    public String bg;
    public final ScheduledExecutorService removeGameRoomScheduler = LizThreadManager.getInstance().getFixExecutorServiceByName("remove_game_room", 8);
    public boolean isEnd = false;

    public FightingManager(Room room) {
        this.room = room;
    }

//    public FightingExtension getFightingExtension() {
//        return fightingExtension;
//    }

    public FightingHandler getFightingHandler() {
        return FightingHandler.getInstance();
    }

    public MatchState getState() {
        return state;
    }

    public void setState(MatchState state) {
        this.state = state;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public BasePlayer getCurrentTurnPlayer() {
        return players.get(currentTurn);
    }

    public HumanPlayer getPlayerByUID(String uid) {
        for (BasePlayer player : players) {
            if (player.getPlayerType() == EPlayerType.HUMAN) {
                if (((HumanPlayer) player).uid.equalsIgnoreCase(uid)) return (HumanPlayer) player;
            }
        }

        return null;
    }

    /**
     * chỉ định sinh ra các diamond đặc biệt
     *
     * @param recMove
     * @param assignMap
     * @param comboList
     * @param isFirstCombo
     */
    public void putAssignMap(RecMove recMove, Map<String, Node> assignMap, List<List<Node>> comboList, boolean isFirstCombo) {
        List<Node> currentMove = new ArrayList<>();
        currentMove.add(new Node().setRow(recMove.from.getInt(Params.X)).setCol(recMove.from.getInt(Params.Y)));
        if (recMove.to != null) {
            currentMove.add(new Node().setRow(recMove.to.getInt(Params.X)).setCol(recMove.to.getInt(Params.Y)));
        }

        for (List<Node> combo : comboList) {
            if (combo.size() == 4) {
                Node assignNode = null;
                if (isFirstCombo) {   //combbo lần đầu, xét đến vị trí vừa bị move
                    for (Node node : currentMove) {   //tì trong combo vị trí vừa bị move
                        for (Node nodeOncombo : combo) {
                            if (node.samePosition(nodeOncombo)) {
                                assignNode = nodeOncombo;
                            }
                        }
                    }

                    //ko tìm thấy thì random
                    if (assignNode == null) {
                        combo.get(Utils.randomInRange(0, combo.size() - 1));
                    }
                } else {

                    //ko phải combo đầu -> luôn random
                    assignNode = combo.get(Utils.randomInRange(0, combo.size() - 1));
                }

                assignMap.put(assignNode.row + "," + assignNode.col, assignNode.cloneNode().setDiamond(Diamond.getBombFromNomal(assignNode.diamond)));
            }

            if (combo.size() > 4) {
                Node assignNode = null;
                if (isFirstCombo) {   //combbo lần đầu, xét đến vị trí vừa bị move
                    for (Node node : currentMove) {   //tì trong combo vị trí vừa bị move
                        for (Node nodeOncombo : combo) {
                            if (node.samePosition(nodeOncombo)) {
                                assignNode = nodeOncombo;
                            }
                        }
                    }

                    //ko tìm thấy thì random
                    if (assignNode == null) {
                        combo.get(Utils.randomInRange(0, combo.size() - 1));
                    }
                } else {

                    //ko phải combo đầu -> luôn random
                    assignNode = combo.get(Utils.randomInRange(0, combo.size() - 1));
                }

                assignMap.put(assignNode.row + "," + assignNode.col, assignNode.cloneNode().setDiamond(Diamond.getFlashFromNomal(assignNode.diamond)));
            }
        }
    }

    public void initPlayer() {
        for (BasePlayer player : players) {
            player.initBefoMatch();
            if(player.getPlayerType() == EPlayerType.NPC){
                //boss mode
                ((NPCPlayer) player).applyBossMode();
            }
        }


    }

    public void initHp() {
        for (BasePlayer player : players) {
            player.initHP();
        }
    }

    public abstract void onPlayerJoin(User user);

    /**
     * khí tất cả player đã vào đủ
     */
    public abstract void onAllPlayerJoin();

    public abstract void startGame(int firstTurn);

    public abstract void move(User user, RecMove recMove);

    public abstract void flee(User user);

    public void stopAllSchedule() {

    }

    public BasePlayer getPlayer(int targetPlayerIndex) {
        return players.get(targetPlayerIndex);
    }

    public String genActorID() {
        return String.valueOf(actorIDCounter++);
    }

    public static void main(String[] args) {
//        int i = 0;
//        //System.out.println(i++);
//        //System.out.println(i);
    }

    public void onEndGame(ISFSObject endGameData) {
        //remove room
        state = MatchState.END;
        //long uid = endGameData.getLong(Params.UID);
        //removeRoom(3);
    }

    public void leaveCurrentRoom(User user) {
        ExtensionUtility.getInstance().leaveRoom(user, this.getRoom());
        this.getRoom().removeVariablesCreatedByUser(user);
    }

    public void removeRoom(int delay) {
        System.out.println("remove room 2: " + room.getName());
        ExtensionUtility.getInstance().removeRoom(this.room);
//        removeGameRoomScheduler.schedule(() -> {
//            stopAllSchedule();
//            ExtensionUtility.getInstance().removeRoom(this.room);
//        }, delay, TimeUnit.SECONDS);
    }


    public abstract ISFSObject checkEndGame(boolean isLastTurn);

    public abstract List<ActionResult> befoTurn();

    public void sageSkilling(User user, String skillID) {
        String uid = user.getName();
        SendSageSkill sendSageUltimate = new SendSageSkill();
        HumanPlayer player = getPlayerByUID(uid);
        Skill skill = player.sage.getSkillByID(skillID);
        if (skill == null) return;

        //nếu đánh ulti
        if (player.sage.isUtilmate(skill)) {
            //du dieu kien ultimate
            if (!player.sage.canUltimate(true)) {
                return;
            }
        } else {
            //skill thường, check số lần sử dụng
            if (!player.sage.canActiveSkill(true, skillID)) {
                return;
            }
            player.sage.useActiveSkill(skillID);
        }

        //do skill
        sendSageUltimate.pushAction(player.sage.action(new SkillingAction(null, skill, 0, true, false, false)));

        //check end game
        ISFSObject endGameData = checkEndGame(false);
        if (endGameData != null) {
            sendSageUltimate.pushEndGameData(endGameData);
            onEndGame(endGameData);
            getFightingHandler().send(sendSageUltimate, user);
            this.leaveCurrentRoom(user);
            return;
        }

        getFightingHandler().send(sendSageUltimate, user);
    }

    public synchronized void heroUltilmate(User user, String actorID) {
        SendHeroSkill sendHeroSkill = new SendHeroSkill();
        if (state == MatchState.END) {
            sendHeroSkill.error = true;
            sendHeroSkill.errorInfo = actorID + ": đã end game";
            getFightingHandler().send(sendHeroSkill, user);
            return;
        }
        String uid = user.getName();
        sendHeroSkill.fightingManager = this;

        BasePlayer player = getPlayerByUID(uid);
        TeamSlot teamSlot = player.getTeamSlotByActorID(actorID);

        if (teamSlot == null) {
            return;
        }

        if (!teamSlot.getCharacter().canUltimate(true, sendHeroSkill, actorID)) {
            getFightingHandler().send(sendHeroSkill, user);
            Logger.getLogger("catch").info("chua du mana ultimate hero " + actorID + " : " + sendHeroSkill.errorInfo + "," + teamSlot.getCharacter().getCurrentEP());
            return;
        }

        //du dieu kien ultimate
        Skill skill = teamSlot.getCharacter().getSkill(3);  //dùng ultilmate
        if (skill == null) {
            return;
        }

        //do skill
        sendHeroSkill.pushAction(teamSlot.getCharacter().action(new SkillingAction(null, skill, 0, true, false, false)));

        //check end game
        ISFSObject endGameData = checkEndGame(false);
        if (endGameData != null) {
            sendHeroSkill.pushEndGameData(endGameData);
            onEndGame(endGameData);
            getFightingHandler().send(sendHeroSkill, user);
            this.leaveCurrentRoom(user);
            return;
        }

        getFightingHandler().send(sendHeroSkill, user);
    }

    public void celestialUltilmate(User user) {
        String uid = user.getName();
        SendCelestialUltilmate sendCelestialUltilmate = new SendCelestialUltilmate();

        HumanPlayer player = getPlayerByUID(uid);
        if (!player.celestial.canUltimate(true)) {
            return;
        }

        //luôn đánh ultilmate
        Skill skill = player.celestial.getSkill(0);
        if (skill == null) return;

        //do skill
        sendCelestialUltilmate.pushAction(player.celestial.action(new SkillingAction(null, skill, 0, true, false, false)));

        //check end game
        ISFSObject endGameData = checkEndGame(false);
        if (endGameData != null) {
            sendCelestialUltilmate.pushEndGameData(endGameData);
            onEndGame(endGameData);
            getFightingHandler().send(sendCelestialUltilmate, user);
            this.leaveCurrentRoom(user);
            return;
        }

        getFightingHandler().send(sendCelestialUltilmate, user);
    }

    public boolean haveDiePlayer() {
        //check chết hết hero
        for (BasePlayer player : players) {
            if (!player.isLive()) {
                return true;
            }
        }

        return false;
    }

    public List<ActorStatistical> getStatisticals() {
        List<ActorStatistical> actorStatisticals = new ArrayList<>();
        for (BasePlayer basePlayer : players) {
            for (TeamSlot teamSlot : basePlayer.getTeam()) {
                if (teamSlot.haveCharacter()) {
                    actorStatisticals.add(teamSlot.getCharacter().getActorStatistical());
                }
            }

            if (basePlayer.sage != null) {
                actorStatisticals.add(basePlayer.sage.getActorStatistical());
            }

            if (basePlayer.celestial != null) {
                actorStatisticals.add(basePlayer.celestial.getActorStatistical());
            }

        }

        return actorStatisticals;
    }

    public Map<String, Object> getPlayerStatisticals(BasePlayer player) {
        Map<String, Object> statisticals = new ConcurrentHashMap<>();
        int totalDamage = 0;
        int totalTank = 0;
        int totalHeal = 0;
        for (TeamSlot teamSlot : player.getTeam()) {
            if (teamSlot.haveCharacter()) {
                totalDamage += teamSlot.getCharacter().getActorStatistical().damage;
                totalTank += teamSlot.getCharacter().getActorStatistical().damageTaken;
                totalHeal += teamSlot.getCharacter().getActorStatistical().healing;
            }
        }
        statisticals.put(Params.DAMAGE, totalDamage);
        statisticals.put(Params.TANK, totalTank);
        statisticals.put(Params.HEAL, totalHeal);
        return statisticals;
    }

    /**
     * Tổng máu bị mất của 1 player
     * @return
     */
    public int getPlayerLostBlood(BasePlayer player){
        int lostBlood = 0;
        for (TeamSlot teamSlot : player.getTeam()) {
            if (teamSlot.haveCharacter()) {
                lostBlood += teamSlot.getCharacter().getActorStatistical().lostBlood;
            }
        }
        return lostBlood;
    }

    /**
     * Tổng số lần hero chết của 1 player
     * @return
     */
    public int getHeroDieCount(BasePlayer player){
        int dieCount = 0;
        for (TeamSlot teamSlot : player.getTeam()) {
            if (teamSlot.haveCharacter()) {
                dieCount += teamSlot.getCharacter().getActorStatistical().dieCount;
            }
        }
        return dieCount;
    }

    /**
     * chọ target ưu tiên cho Utilmate
     * @param user
     * @param actorID
     */
    public void selectTarget(User user, String actorID) {
        BasePlayer player = getPlayerByUID(user.getName());
        TeamSlot target = getTeamSlotByActorID(actorID);
        if(target != null){
            if(target.getPlayer() == player){   //target bên mình
                player.allyTarget = target;
            }else { //target bên địch
                player.target = actorID;
            }
        }
    }

    public TeamSlot getTeamSlotByActorID(String actorID){
        for(BasePlayer basePlayer : players){
            for(TeamSlot teamSlot : basePlayer.team){
                if(teamSlot.haveCharacter() && teamSlot.getCharacter().getActorID().equalsIgnoreCase(actorID)){
                    return teamSlot;
                }
            }
        }

        return null;
    }

    public void startMisson(){

    };

    public abstract int getCampaignArea();
    public abstract int getCampaignStation();

    public final Room getRoom() {
        return this.room;
    }
}
