package com.bamisu.log.gameserver.module.ingame.entities.player;

import com.bamisu.log.gameserver.module.campaign.entities.TeamUtils;
import com.bamisu.log.gameserver.module.characters.kingdom.Kingdom;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action.EffectApplyAction;
import com.bamisu.log.gameserver.module.ingame.entities.character.*;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingManager;
import com.bamisu.gamelib.skill.SkillConfigManager;
import com.bamisu.gamelib.skill.config.entities.TeamEffect;
import com.bamisu.gamelib.skill.passive.Statbuff;
import com.bamisu.gamelib.utils.business.Debug;
import com.smartfoxserver.v2.entities.Room;

import java.util.*;

/**
 * Create by Popeye on 5:47 PM, 1/14/2020
 */
public abstract class BasePlayer {
//    FightingManager fightingManager;
    public List<TeamSlot> team;
    public SageIngame sage;
    public CelestialIngame celestial;
    private EPlayerType playerType;
    public String target;  //target ưu tiên khi hero đánh Utilmate
    public TeamSlot allyTarget;  //target ưu tiên khi hero đánh Utilmate
    private boolean havePuzzleBoard = false;
    public int actorActionNumber = 0; //lưu lại số lượng hero đã đánh trong turn trước đó
    public List<TeamSlot> willSkilling = new ArrayList<>(); //hero sẽ đánh ở lượt tiếp theo (áp dụng cho enemy là NPC)

    public Room room;

    public BasePlayer(Room room, boolean havePuzzleBoard) {
//        this.fightingManager = fightingManager;
        this.room = room;
        this.team = new ArrayList<>();
        this.havePuzzleBoard = havePuzzleBoard;
    }

    public FightingManager getFightingManager() {
        return (FightingManager) this.room.getProperty("manager");
    }

    public List<TeamSlot> getTeam() {
        return team;
    }

    public abstract void buildTeam();

    public void applyTeamEffect() {
        TeamEffect teamEffect = getTeamEffect();
        if (teamEffect == null) return;
        Debug.trace("TEAM EFFECT " + teamEffect.id);
        for (TeamSlot teamSlot : team) {
            if (teamSlot.haveCharacter()) {
                for (Statbuff statbuff : teamEffect.statbuff) {
                    teamSlot.getCharacter().action(new EffectApplyAction(
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

    public void applyMapbonus() {
        for (TeamSlot teamSlot : team) {
            if (teamSlot.haveCharacter()) {
                teamSlot.getCharacter().applyMapBonus(getFightingManager().mapBonus);
            }
        }
    }

    public void applyPositionBonus() {
        for (TeamSlot teamSlot : team) {
            if (teamSlot.haveCharacter()) {
                teamSlot.getCharacter().applyPositionBonus();
            }
        }
    }

    public TeamEffect getTeamEffect() {
        List<Kingdom> kingdomList = new ArrayList<>();
        for (TeamSlot teamSlot : team) {
            if (teamSlot.haveCharacter()) {
                kingdomList.add(teamSlot.getCharacter().getKingdom());
            }
        }

        int id = TeamUtils.getTeamBonus(kingdomList);
        if (id == -1) return null;
        return SkillConfigManager.getInstance().getTeamEffect(id);
    }

    public void initBefoMatch() {
        //init Hero
        for (TeamSlot teamSlot : team) {
            teamSlot.initBefoMatch();
        }

        //init Sage
        if (sage != null) {
            sage.initBefoFighting();
        }

        if (celestial != null) {
            celestial.initBefoFighting();
        }
    }

    public void initHP() {
        //init Hero
        for (TeamSlot teamSlot : team) {
            teamSlot.initHP();
        }
    }

    public EPlayerType getPlayerType() {
        return playerType;
    }

    public void setPlayerType(EPlayerType playerType) {
        this.playerType = playerType;
    }

    public int getPlayerID() {
        return getFightingManager().players.indexOf(this);
    }

    /**
     * Kiem tra tat ca cac hero con song khong
     *
     * @return
     */
    public boolean isLive() {
        for (TeamSlot teamSlot : team) {
            if (teamSlot.haveCharacter()) {
                if (teamSlot.getCharacter().isLive()) {
                    return true;
                }
            }
        }

        return false;
    }

    public TeamSlot getTeamSlotByActorID(String actorID) {
        for (TeamSlot teamSlot : team) {
            if (teamSlot.haveCharacter()) {
                if (teamSlot.getCharacter().getActorID().equalsIgnoreCase(actorID)) return teamSlot;
            }
        }

        return null;
    }

    public void clearTankFlag() {
        for (TeamSlot teamSlot : team) {
            if (teamSlot.haveCharacter() && teamSlot.getCharacter().isLive()) {
                teamSlot.getCharacter().setHaveTankForAlly(false);
            }
        }
    }

    public List<Kingdom> getListKingdom() {
        List<Kingdom> kingdomList = new ArrayList<>();
        for (TeamSlot teamSlot : team) {
            if (teamSlot.haveCharacter()) {
                kingdomList.add(teamSlot.getCharacter().getKingdom());
            }
        }

        return kingdomList;
    }

    /**
     * lấy team sắp xếp giảm dần theo AGI
     *
     * @return
     */
    public List<TeamSlot> getTeamSortByAGI() {
        List<TeamSlot> teamSlots = new ArrayList<>(team);
        Collections.sort(teamSlots, new Comparator<TeamSlot>() {
            @Override
            public int compare(TeamSlot o1, TeamSlot o2) {
                if (!o1.haveCharacter()) {
                    return 1;
                }

                if (!o2.haveCharacter()) {
                    return -1;
                }

                if (o1.getCharacter().getAGI() > o2.getCharacter().getAGI()) return -1;
                if (o1.getCharacter().getAGI() < o2.getCharacter().getAGI()) return 1;

                return 0;
            }
        });
        return teamSlots;
    }

    /**
     * lấy team sắp xếp giảm dần theo AGI
     *
     * @return
     */
    public void sortTeamByAGI(List<TeamSlot> teamSlots) {
        Collections.sort(teamSlots, new Comparator<TeamSlot>() {
            @Override
            public int compare(TeamSlot o1, TeamSlot o2) {
                if (!o1.haveCharacter()) {
                    return 1;
                }

                if (!o2.haveCharacter()) {
                    return -1;
                }

                if (o1.getCharacter().getAGI() > o2.getCharacter().getAGI()) return -1;
                if (o1.getCharacter().getAGI() < o2.getCharacter().getAGI()) return 1;

                return 0;
            }
        });
    }

    public BasePlayer getEnemyPlayer(){
        int targetPlayerIndex = 0;
        if (getPlayerID() == 0) {
            targetPlayerIndex = 1;
        }
        return getFightingManager().getPlayer(targetPlayerIndex);
    }

    public boolean havePuzzleBoard(){
        return havePuzzleBoard;
    }

    public List<TeamSlot> getLiveTeamSlots() {
        List<TeamSlot> liveTeamSlot = new ArrayList<>();
        for (TeamSlot teamSlot : getTeam()) {
            if (teamSlot.haveCharacter()) {
                if (teamSlot.getCharacter().isLive()) {
                    liveTeamSlot.add(teamSlot);
                }
            }
        }
        return liveTeamSlot;
    }

    public Collection<String> getWillSkilling(){
        if(willSkilling == null) return new ArrayList<>();

        Collection<String> willSkillingList = new ArrayList<>();
        for(TeamSlot teamSlot : willSkilling){
            willSkillingList.add(teamSlot.getCharacter().getActorID());
        }
        return willSkillingList;
    }
}
