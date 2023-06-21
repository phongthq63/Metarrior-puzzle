package com.bamisu.log.gameserver.module.ingame.entities.fighting;

import com.bamisu.gamelib.fighting.NPCSkillSelecter;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.ingame.cmd.rec.RecMove;
import com.bamisu.log.gameserver.module.ingame.cmd.send.MovePackage;
import com.bamisu.log.gameserver.module.ingame.cmd.send.SendJoinRoom;
import com.bamisu.log.gameserver.module.ingame.cmd.send.SendMove;
import com.bamisu.log.gameserver.module.ingame.entities.Diamond;
import com.bamisu.log.gameserver.module.ingame.entities.MatchState;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action.*;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action_result.ActionResult;
import com.bamisu.log.gameserver.module.ingame.entities.character.Character;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.effect.Effect;
import com.bamisu.log.gameserver.module.ingame.entities.effect.instance.SE_Bleed;
import com.bamisu.log.gameserver.module.ingame.entities.effect.instance.SE_Invigorated;
import com.bamisu.log.gameserver.module.ingame.entities.effect.instance.SE_Poisoned;
import com.bamisu.log.gameserver.module.ingame.entities.player.BasePlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.EPlayerType;
import com.bamisu.log.gameserver.module.ingame.entities.player.HumanPlayer;
import com.bamisu.log.gameserver.module.ingame.entities.player.TeamSlot;
import com.bamisu.log.gameserver.module.ingame.entities.skill.*;
import com.bamisu.log.gameserver.module.skill.DamageType;
import com.bamisu.log.gameserver.module.skill.Skill;
import com.bamisu.log.gameserver.module.skill.SkillType;
import com.bamisu.log.gameserver.module.skill.SkillUtils;
import com.bamisu.log.gameserver.module.skill.template.passive.PassiveSkillTemplateProps;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.gamelib.skill.passive.Statbuff;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by Popeye on 5:34 PM, 1/14/2020
 */
public abstract class PvMManager extends FightingManager {
    public long uid;
    public List<String> winConditions;
    public static final int combosIncRate = 10;

    public PvMManager(Room room) {
        super(room);
        this.type = FightingType.PvM;
        this.uid = (long) room.getProperty(Params.UID);
        this.winConditions = (List<String>) room.getProperty(Params.WIN_CONDITIONS);
    }

    @Override
    public void onPlayerJoin(User user) {
        players.add(new HumanPlayer(this.getRoom(), String.valueOf(uid), true));
        onAllPlayerJoin();
    }

    @Override
    public void onAllPlayerJoin() {

    }

    @Override
    public void startGame(int firstTurn) {
        //tạo table
        puzzleTable.genMatrixWithNoCombo();

        //fill special puzzle
        puzzleTable.fillSpecialPuzzle(this);

        initPlayer();
        setCurrentTurn(firstTurn);
        setState(MatchState.MOVING);

        //init Team effect
        initTeamEffect();

        //initPassive
        initPassive();

        //init pvp offline bonus effect
        if (function == EFightingFunction.PvP_FRIEND || function == EFightingFunction.PvP_ARENA) {
            initDefenderEffect();
        }

        //initHP
        initHp();

        //start fighting efect
        List<ActionResult> startFightingActions = startFightingEffect();

        randomEnemyWillSkilling();

        //send notify
        User user = ExtensionUtility.getInstance().getUserById(uid);
        if (user != null) {
            SendJoinRoom sendJoinRoom = new SendJoinRoom();
            sendJoinRoom.manager = this;
            sendJoinRoom.yourID = 0;
            sendJoinRoom.currentTurn = firstTurn;
            sendJoinRoom.startFightingActions = startFightingActions;
            sendJoinRoom.willFight = getPlayer(1).getWillSkilling();
            sendJoinRoom.packStartFightingContext();
            sendJoinRoom.packStartFightingActions();
            getFightingHandler().send(sendJoinRoom, user);
        }

        //first turn is offline
        if (getCurrentTurnPlayer().getPlayerType() == EPlayerType.NPC) {
            SendMove sendMove = new SendMove();
            sendMove.fightingManager = this;
            MovePackage nexturn;
            nexturn = npcTurn();
            sendMove.pushPackage(nexturn.packData());
            sendMove.willFight = getPlayer(1).getWillSkilling();
            getFightingHandler().broadcast(sendMove, getCurrentTurnPlayer().room);

        }
    }

    private void initTeamEffect() {
        for (BasePlayer player : players) {
            player.applyMapbonus();
            player.applyTeamEffect();
            player.applyPositionBonus();
        }
    }

    private void initDefenderEffect() {
        for (TeamSlot teamSlot : players.get(1).team) {
            if (teamSlot.haveCharacter()) {
                teamSlot.getCharacter().applyPvPOfflineDefenderBonus();
            }
        }
    }

    private void initPassive() {
        for (BasePlayer player : players) {
            //Hero
            for (TeamSlot teamSlot : player.team) {
                if (teamSlot.haveCharacter() && teamSlot.getCharacter().isLive()) {
                    if (teamSlot.getCharacter().getType() == ECharacterType.Hero || teamSlot.getCharacter().getType() == ECharacterType.MiniBoss || teamSlot.getCharacter().getType() == ECharacterType.Boss) {
                        Skill passiveSkill = teamSlot.getCharacter().getSkill(0);
                        PassiveSkillTemplateProps props = passiveSkill.getTamplatePropsAsPassive();
                        if (props.statbuff != null) {
                            for (Statbuff statbuff : props.statbuff) {
                                //dependentHeroCheck
                                boolean dependentHeroCheck = false;
                                if (statbuff.dependentHero.isEmpty()) {
                                    dependentHeroCheck = true;
                                } else {
                                    for (TeamSlot teamSlot1 : player.team) {
                                        if (teamSlot1.haveCharacter()) {
                                            if (teamSlot1.getCharacter().getCharacterVO().readID().equalsIgnoreCase(statbuff.dependentHero)) {
                                                dependentHeroCheck = true;
                                            }
                                        }
                                    }
                                }
                                if (!dependentHeroCheck) continue;
                                //

                                List<Character> targets = SkillUtils.findBuffTarget(statbuff.target, teamSlot.getCharacter());
                                for (Character target : targets) {
                                    target.action(new EffectApplyAction(
                                            teamSlot.getCharacter(),
                                            EEffect.Stat_Buff,
                                            999,
                                            Arrays.asList(
                                                    statbuff.attr,
                                                    teamSlot.getCharacter().calculaRateStatBuff(statbuff),
                                                    1000
                                            ),
                                            false));
                                }
                            }
                        }
                    }
                }
            }

            //Celestial
            if (player.celestial != null) {
                for (Skill skill : player.celestial.getSkills()) {
                    if (skill.getSkillBaseInfo().type == SkillType.PASSIVE.getIntValue()) {
                        PassiveSkillTemplateProps props = skill.getTamplatePropsAsPassive();
                        if (props.statbuff != null) {
                            for (Statbuff statbuff : props.statbuff) {
                                List<Character> targets = SkillUtils.findBuffTarget(statbuff.target, player.celestial);
                                for (Character target : targets) {
                                    target.action(new EffectApplyAction(
                                            player.celestial,
                                            EEffect.Stat_Buff,
                                            999,
                                            Arrays.asList(
                                                    statbuff.attr,
                                                    player.celestial.calculaRateStatBuff(statbuff),
                                                    1000
                                            ),
                                            false));
                                }
                            }
                        }
                    }
                }
            }

            ////Sage
            if (player.sage != null) {
                for (Skill skill : player.sage.getOtherPassiveSkills()) {
                    if (skill.getSkillBaseInfo().type == SkillType.PASSIVE.getIntValue()) {
                        PassiveSkillTemplateProps props = skill.getTamplatePropsAsPassive();
                        if (props.statbuff != null) {
                            for (Statbuff statbuff : props.statbuff) {
                                List<Character> targets = SkillUtils.findBuffTarget(statbuff.target, player.sage);
                                for (Character target : targets) {
                                    target.action(new EffectApplyAction(
                                            player.sage,
                                            EEffect.Stat_Buff,
                                            999,
                                            Arrays.asList(
                                                    statbuff.attr,
//                                                    1.0f,
                                                    player.sage.calculaRateStatBuff(statbuff),
                                                    1000
                                            ),
                                            false));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * xử lý các actions khi bắt đầu trận đấu
     *
     * @return
     */
    public List<ActionResult> startFightingEffect() {
        List<ActionResult> results = new ArrayList<>();

        //test
//        for (BasePlayer player : players) {
//            for (TeamSlot teamSlot : player.getTeam()) {
//                if (teamSlot.haveCharacter()) {
//                    //random cộng mana
//                    if (Utils.rate(20)) {
//                        teamSlot.getCharacter().action(new EnergyChangeAction(Arrays.asList(Utils.randomInRange(10, 30))));
//                    }
//
//                    //random bị stun
//                    if (Utils.rate(20)) {
//                        teamSlot.getCharacter().action(new EffectApplyAction(Arrays.asList(EEffect.Stunned, Utils.randomInRange(1, 3))));
//                    }
//                }
//            }
//        }

        return results;
    }

    @Override
    public void move(User user, RecMove recMove) {
        MovePackage movePackage = new MovePackage();
        Map<Diamond, Integer> totalComboMap = new ConcurrentHashMap<>();
//        int diamondCount = 0;
//        int MinorCount = 1;
        int majorCount = 1;

        for (int i = Diamond.RED.getValue(); i <= Diamond.PURPLE.getValue(); i++) {
            totalComboMap.put(Diamond.fromID(i), 0);
        }

        for (Combo combo : recMove.combos) {
            try {
                totalComboMap.put(combo.diamond, totalComboMap.get(combo.diamond) + combo.count);
//                diamondCount += combo.count;
            } catch (NullPointerException nullEx) {
                Logger.getLogger("catch").info(totalComboMap + "\n" + recMove.combos.indexOf(combo) + "\n" + recMove.data.toJson());
            }
        }

        //tính final color combo
        Map<Diamond, FinalColorCombo> finalColorComboMap = new HashMap<>();     //key là color
        //xác định type của final combo
        for (Diamond diamond : totalComboMap.keySet()) {
            FinalColorCombo finalColorCombo = new FinalColorCombo();
            finalColorCombo.diamond = diamond;
            finalColorCombo.count = totalComboMap.get(diamond);
            finalColorCombo.rate = recMove.comboCount * combosIncRate;
            if (totalComboMap.get(diamond) < majorCount) {
                finalColorCombo.type = ComboType.NONE;
                finalColorCombo.isCrit = false;
//            } else if (totalComboMap.get(diamond) < majorCount) {
//                finalColorCombo.type = ComboType.SKILL2;
//                finalColorCombo.isCrit = false;
            } else {
                finalColorCombo.type = ComboType.SKILL3;
                finalColorCombo.isCrit = false;
            }
            finalColorComboMap.put(diamond, finalColorCombo);
        }

        //doSkill
        BasePlayer player = getPlayerByUID(user.getName());

        //tinh diamond thua
        int bonusManaSage = 0;
        int bonusManaCelestial = 0;
        int bonusManaSageActiveSkill = 0;
        for (Diamond diamond : finalColorComboMap.keySet()) {
            if (finalColorComboMap.get(diamond).count <= 0) continue;

            //check có hero màu này đang sống k
            boolean dontHave = false;
            for (TeamSlot teamSlot : player.getTeam()) {
                if (teamSlot.haveCharacter() && teamSlot.getCharacter().isLive() && teamSlot.getCharacter().getElement().getId() == diamond.getElement().getId()) {
                    dontHave = true;
                }
            }
            if (!dontHave) {  //ko có hero màu này
//                bonusManaSage += finalColorComboMap.get(diamond).count * 12;
//                bonusManaCelestial += finalColorComboMap.get(diamond).count * 5;
//
//                bonusManaSageActiveSkill += finalColorComboMap.get(diamond).count * 5;
            } else {
                if (finalColorComboMap.get(diamond).count > majorCount) {
                    bonusManaSage += (finalColorComboMap.get(diamond).count - majorCount) * 8;
                    bonusManaCelestial += (finalColorComboMap.get(diamond).count - majorCount) * 4;
                }
            }
        }
        //cộng mana cho skill active của pháp sư
        if(player.sage != null){
            player.sage.changeManaACtiveSkill(bonusManaSageActiveSkill);
        }

        fight(player, movePackage, finalColorComboMap, bonusManaSage, bonusManaCelestial);

        //send move
        SendMove sendMove = new SendMove();
        sendMove.fightingManager = this;

        //check end game
        ISFSObject endGameData = checkEndGame(false);
        if (endGameData != null) {
            movePackage.pushEndGameData(endGameData);
            onEndGame(endGameData);
            sendMove.pushPackage(movePackage.packData());
            getFightingHandler().broadcast(sendMove, user.getLastJoinedRoom());
            this.leaveCurrentRoom(user);
            return;
        }


        //switch turn
        switchTurn();

        //add mana to active skill sage
        getPlayer(0).sage.changeManaACtiveSkill(20);

        //befo next turn
        movePackage.pushBefoNextTurn(befoTurn());

        //check end game
        endGameData = checkEndGame(false);
        if (endGameData != null) {
            movePackage.pushEndGameData(endGameData);
            onEndGame(endGameData);
            sendMove.pushPackage(movePackage.packData());
            getFightingHandler().broadcast(sendMove, user.getLastJoinedRoom());
            this.leaveCurrentRoom(user);
            return;
        }
        sendMove.pushPackage(movePackage.packData());

        //next turn is NPC
        if (players.get(getCurrentTurn()).getPlayerType() == EPlayerType.NPC) {
            MovePackage nexturn;
            nexturn = npcTurn();
            sendMove.pushPackage(nexturn.packData());
        }
        //send to all user
        sendMove.willFight = getPlayer(1).getWillSkilling();
        getFightingHandler().broadcast(sendMove, user.getLastJoinedRoom());
        turnCount++;
        setState(MatchState.MOVING);
        //remove room
        endGameData = checkEndGame(false);
        if (endGameData != null) {
            this.leaveCurrentRoom(user);
        }
    }

    public void switchTurn() {
        setCurrentTurn(Math.abs(1 - getCurrentTurn()));
    }

    public MovePackage npcTurn() {
        MovePackage movePackage = new MovePackage();

        BasePlayer player = getCurrentTurnPlayer();
        fight(player, movePackage, null, 0, 0);

        //check end game
        ISFSObject endGameData = checkEndGame(turnCount == maxTurn);
        if (endGameData != null) {
            movePackage.pushEndGameData(endGameData);
            onEndGame(endGameData);
        }

        //switch turn
        switchTurn();

        //befo next turn
        movePackage.pushBefoNextTurn(befoTurn());
        //check end game
        endGameData = checkEndGame(false);
        if (endGameData != null) {
            movePackage.pushEndGameData(endGameData);
            onEndGame(endGameData);
        }

        return movePackage;
    }

    /**
     * kiểm tra kết thúc game
     *
     * @return
     */
    public abstract ISFSObject checkEndGame(boolean isLastTurn);

    /**
     * chọn trước enemy được đánh trong turn tiếp theo
     */
    public void randomEnemyWillSkilling() {
        BasePlayer humanPlayer = getPlayer(0);
        BasePlayer npcPlayer = getPlayer(1);

        int number;
        if (humanPlayer.actorActionNumber == 0) {
            number = Utils.randomInRange(1, 3);
        } else {
            number = Utils.randomInRange(
                    humanPlayer.actorActionNumber,
                    humanPlayer.actorActionNumber + 2 > 5 ? 5 : humanPlayer.actorActionNumber + 2
            );
        }

        List<TeamSlot> liveTeamSlot = npcPlayer.getLiveTeamSlots();
        if (number >= liveTeamSlot.size()) {
            npcPlayer.willSkilling = liveTeamSlot;
        } else {
            Collections.shuffle(liveTeamSlot);
            npcPlayer.willSkilling = liveTeamSlot.subList(0, number);
        }
    }

    /**
     * đánh skill dựa vào diamond
     *
     * @param movePackage
     * @param finalColorComboMap
     */
    public void fight(BasePlayer player, MovePackage movePackage, Map<Diamond, FinalColorCombo> finalColorComboMap, int bonusManaSage, int bonusManaCelestial) {
        movePackage.turnCount = turnCount;
        movePackage.currentTurn = getCurrentTurn();
        movePackage.nextTurn = getCurrentTurn();
        movePackage.target = Math.abs(1 - getCurrentTurn());

        List<ActionResult> listFirstAction = new ArrayList<>();
        List<ActionResult> listLastAction = new ArrayList<>();

        //TODO: người đánh
        if (player.getPlayerType() == EPlayerType.HUMAN) {
            int countFight = 0;

            //tang mana PS LT
            if (bonusManaSage > 0) {
                if (player.sage != null) {
                    listFirstAction.addAll(player.sage.action(new EnergyChangeAction(bonusManaSage, false)));
                }
            }

            if(bonusManaCelestial > 0){
                if (player.celestial != null) {
                    listFirstAction.addAll(player.celestial.action(new EnergyChangeAction(bonusManaCelestial, false)));
                }
            }

            //doSkill logic
            List<TeamSlot> team = player.getTeamSortByAGI();
            for (TeamSlot slot : team) {
                //xác định có đánh hay ko và đánh skill gì bao nhiêu % dame
                if (slot.haveCharacter() && slot.getCharacter().isLive()) {
                    Diamond diamond = Diamond.fromElement(slot.getCharacter().getElement());
                    if (finalColorComboMap.containsKey(diamond)) {
                        FinalColorCombo finalColorCombo = finalColorComboMap.get(diamond);

                        //tăng mana cho hero dựa theo puzzle cùng màu ăn được
                        if (finalColorCombo.count > 0) {
                            listFirstAction.addAll(slot.getCharacter().action(new EnergyChangeAction(finalColorCombo.count * 10, false)));
                        }

                        //đánh skill nếu có thể
                        if (slot.getCharacter().canSkillActivate()) {
                            switch (finalColorCombo.type) {
                                case SKILL2:
                                    listLastAction.addAll(slot.doSkill(1, finalColorCombo.rate, false, false));
                                    countFight++;
                                    break;
                                case SKILL3:
                                    listLastAction.addAll(slot.doSkill(2, finalColorCombo.rate, false, false));
                                    countFight++;
                                    break;
                            }
                        }
                    }
                }
            }

            player.actorActionNumber = countFight;
        }

        movePackage.pushAction(listFirstAction);
        movePackage.pushAction(listLastAction);

        //TODO: NPC đánh
        if (player.getPlayerType() == EPlayerType.NPC) {
            if (player.sage != null) {
                if (player.sage.canUltimate()) {
                    movePackage.pushAction(player.sage.action(new SkillingAction(null, player.sage.getSkill(0), 0, true, false, false)));
                }
            }

            if (player.celestial != null) {
                if (player.celestial.canUltimate()) {
                    movePackage.pushAction(player.celestial.action(new SkillingAction(null, player.celestial.getSkill(0), 0, true, false, false)));
                }
            }

            List<TeamSlot> listSlotHaveCharacter = new ArrayList<>();
            for (TeamSlot slot : player.team) {
                if (slot.haveCharacter()) {
                    listSlotHaveCharacter.add(slot);
                }
            }
            player.sortTeamByAGI(listSlotHaveCharacter);

            List<TeamSlot> willFightNormal = new ArrayList<>();

            //đánh Ultimate trước
            for (TeamSlot slot : listSlotHaveCharacter) {
                if (slot.getCharacter().isLive() && slot.getCharacter().canUltimate()) {
                    //đánh ultilmate
                    int ultilmateSkillIndex = slot.getCharacter().getUtilsSkillIndex();
                    movePackage.pushAction(slot.doSkill(ultilmateSkillIndex, 0, false));
                }

                //sẽ đánh normal
                if(player.willSkilling.contains(slot)){
                    willFightNormal.add(slot);
                }
            }

            //đánh skill normal trước
            for (TeamSlot slot : willFightNormal) {
                if (slot.getCharacter().isLive() && slot.getCharacter().canSkillActivate()) {
                    //30:50:20
                    int skillIndex = -1;
                    boolean crit = false;
                    switch (NPCSkillSelecter.getInstance().randomSkill()) {
                        case "min":
                            skillIndex = slot.getCharacter().getMinSkill();
                            break;
                        case "maj":
                            skillIndex = slot.getCharacter().getMajSkill();
                            break;
                        case "max":
                            skillIndex = slot.getCharacter().getMajSkill();
                            crit = true;
                            break;
                    }
                    movePackage.pushAction(slot.doSkill(skillIndex, 0, false, crit));
                }
            }
            randomEnemyWillSkilling();
        }
    }

    @Override
    public List<ActionResult> befoTurn() {
        List<ActionResult> results = new ArrayList<>();

        for (BasePlayer player : players) {
            for (TeamSlot teamSlot : player.team) {
                if (teamSlot.haveCharacter()) {
                    teamSlot.getCharacter().berfoturn();
                }
            }
        }

        //count down and clear SE
        results.addAll(countdownAndClearSE());

        //action SE effect
        results.addAll(acctionEffectBefoturn());

        //các action của SE
        BasePlayer currentTurnPlayer = getCurrentTurnPlayer();

        for (TeamSlot teamSlot : currentTurnPlayer.team) {
            if (teamSlot.haveCharacter() && teamSlot.getCharacter().isLive()) {
                Character character = teamSlot.getCharacter();
                if (character.getType() == ECharacterType.Hero || character.getType() == ECharacterType.Boss || character.getType() == ECharacterType.MiniBoss) {
                    if (character.canSkillPassive()) {
                        Skill skill = character.getSkill(0);
                        results.addAll(character.action(new SkillingAction(MatchState.BEFOTURNING, skill, 0, false, true, false)));
                    }
                }
            }
        }

        // ON END SKILL
        for (BasePlayer player : players) {
            for (TeamSlot teamSlot : player.team) {
                if (teamSlot.haveCharacter() && teamSlot.getCharacter().isLive()) {
                    Character character = teamSlot.getCharacter();
                    if (character.getType() == ECharacterType.Hero || character.getType() == ECharacterType.Boss || character.getType() == ECharacterType.MiniBoss) {
                        Skill skill = character.getSkill(0);
                        skill.setCanMiss(false);
                        results.addAll(character.action(new SkillingAction(MatchState.ON_END_SKILL, skill, 0, false, true, false)));
                    }
                }
            }
        }
        return results;
    }

    /**
     * count down và loại bỏ các SE đã hết tác dụng
     *
     * @return
     */
    public List<ActionResult> countdownAndClearSE() {
        List<ActionResult> listActionResult = new ArrayList<>();
        BasePlayer currentTurnPlayer = getCurrentTurnPlayer();
        BasePlayer notCurrentTurnPlayer = currentTurnPlayer.getEnemyPlayer();

        List<Effect> removeList = new ArrayList<>();
        for (TeamSlot teamSlot : notCurrentTurnPlayer.team) {
            removeList.clear();
            if (teamSlot.haveCharacter() && teamSlot.getCharacter().isLive()) {
                for (Effect effect : teamSlot.getCharacter().getEffectList()) {
                    effect.turn--;
                    if (effect.turn <= 0) {
                        removeList.add(effect);
                    }
                }

                if (!removeList.isEmpty()) {
                    listActionResult.addAll(teamSlot.getCharacter().action(new EffectRemoveAction(removeList)));
                }
            }
        }

        //clear shield
        for (TeamSlot teamSlot : notCurrentTurnPlayer.team) {
            if (teamSlot.haveCharacter() && teamSlot.getCharacter().isLive()) {
                if (teamSlot.getCharacter().getShieldAll() > 0) {
                    if (turnCount - teamSlot.getCharacter().getShieldAllLastTurn() >= 2) {
                        listActionResult.addAll(teamSlot.getCharacter().action(new ShieldChangeAction(0, "")));
                    }
                }
            }
        }

        return listActionResult;
    }


    /**
     * action chảy máu
     *
     * @return
     */
    public List<ActionResult> acctionEffectBefoturn() {
        List<ActionResult> listActionResult = new ArrayList<>();
        BasePlayer currentTurnPlayer = getCurrentTurnPlayer();

        //chảy máu khi bị Bleed và Poisoned
        for (TeamSlot teamSlot : currentTurnPlayer.team) {
            if (teamSlot.haveCharacter() && teamSlot.getCharacter().isLive()) {
                for (int i = 0; i < teamSlot.getCharacter().getEffectList().size(); i++) {
                    Effect effect = teamSlot.getCharacter().getEffectList().get(i);
                    if (teamSlot.getCharacter().isLive()) {
                        if (effect.getType() == EEffect.Bleed) {   //hiệu ứng chảy máu sát thương chuẩn
                            int damage = (int) (teamSlot.getCharacter().getMaxHP() * ((SE_Bleed) effect).rate / 100);
                            listActionResult.addAll(teamSlot.getCharacter().action(new HealthChangeAction(effect.getSourceActor(), -damage)));
                        }

                        if (effect.getType() == EEffect.Poisoned) {   //hiệu ứng chảy máu sát thương phép
                            int damage = (int) (teamSlot.getCharacter().getMaxHP() * ((SE_Poisoned) effect).rate / 100);
                            DamagePackge damagePackge = new DamagePackge();
                            damagePackge.push(new Damage(damage, DamageType.MAGIC));
                            listActionResult.addAll(teamSlot.getCharacter().action(new HealthChangeAction(effect.getSourceActor(), -SkillUtils.calculateD2(damagePackge, null, teamSlot.getCharacter()))));
                        }

                        if (effect.getType() == EEffect.Invigorated) {   //hiệu hồi máu mỗi turn
                            int heath = (int) (teamSlot.getCharacter().getMaxHP() * ((SE_Invigorated) effect).rate / 100);
                            listActionResult.addAll(teamSlot.getCharacter().action(new HealthChangeAction(effect.getSourceActor(), heath)));
                        }
                    }
                }
            }
        }
        return listActionResult;
    }

    @Override
    public void flee(User user) {
        this.leaveCurrentRoom(user);
    }
}
