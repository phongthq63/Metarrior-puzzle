package com.bamisu.log.gameserver.module.ingame.entities.player;

import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.characters.entities.*;
import com.bamisu.log.gameserver.module.ingame.entities.actor.action.EffectApplyAction;
import com.bamisu.log.gameserver.module.ingame.entities.character.*;
import com.bamisu.log.gameserver.module.ingame.entities.effect.EEffect;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingManager;
import com.bamisu.gamelib.skill.SkillConfigManager;
import com.bamisu.gamelib.skill.config.BossModeConfig;
import com.bamisu.gamelib.skill.passive.Statbuff;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.business.Debug;
import com.smartfoxserver.v2.entities.Room;

import java.util.Arrays;
import java.util.List;

/**
 * Create by Popeye on 5:48 PM, 1/14/2020
 */
public class NPCPlayer extends BasePlayer {
    List<ICharacter> npcTeam;

    public NPCPlayer(Room room, List<ICharacter> npcTeam, boolean havePuzzleBoard) {
        super(room, havePuzzleBoard);
        setPlayerType(EPlayerType.NPC);
        this.npcTeam = npcTeam;
        buildTeam();
    }

    @Override
    public void buildTeam() {
        switch (getFightingManager().type) {
            case PvM:
                if(npcTeam != null) {
                    for (ICharacter character : npcTeam) {
                        if(character == null){ //slot trống
                            this.team.add(new TeamSlot(this));
                            continue;
                        }

                        if (character.readCharacterType() == ECharacterType.MiniBoss.getType()) {
                            this.team.add(new TeamSlot(new MBossIngame(this, character)));
                        } else if (character.readCharacterType() == ECharacterType.Hero.getType()) {
                            this.team.add(new TeamSlot(new HeroIngame(this, character)));
                        } else if (character.readCharacterType() == ECharacterType.Creep.getType()) {
                            this.team.add(new TeamSlot(new CreepIngame(this, character)));
                        }
                    }
                }

                //sage
                if(this.room.containsProperty(Params.ENEMY_SAGE)) {
                    String jsonDataSage = (String) room.getProperty(Params.ENEMY_SAGE);
                    Sage sage = Utils.fromJson(jsonDataSage, Sage.class);
                    this.sage = new SageIngame(this, sage);
                }

                //celestial
                if(room.containsProperty(Params.ENEMY_CELESTIAL)){
                    String jsonDataCelestial = (String) room.getProperty(Params.ENEMY_CELESTIAL);
                    Celestial celestial = Utils.fromJson(jsonDataCelestial, Celestial.class);
                    this.celestial = new CelestialIngame(this, celestial);
                    //Turn off celestial
                    this.celestial = null;
                }
        }
    }

    public void applyBossMode(){
        Debug.trace("BOSS MODE " + getFightingManager().bossMode);
        if(getFightingManager().bossMode){
            TeamSlot teamSlot = team.get(1);
            teamSlot.getCharacter().setIsBossMode(true);
            BossModeConfig bossModeConfig = SkillConfigManager.getInstance().getBossModeConfig();
            for (Statbuff statbuff : bossModeConfig.statbuff) {
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
