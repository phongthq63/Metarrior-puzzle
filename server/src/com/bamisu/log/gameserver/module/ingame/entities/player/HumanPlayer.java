package com.bamisu.log.gameserver.module.ingame.entities.player;

import com.bamisu.log.gameserver.module.campaign.entities.HeroPackage;
import com.bamisu.log.gameserver.module.characters.entities.Celestial;
import com.bamisu.log.gameserver.module.characters.entities.Hero;
import com.bamisu.log.gameserver.module.characters.entities.Sage;
import com.bamisu.log.gameserver.module.ingame.entities.character.CelestialIngame;
import com.bamisu.log.gameserver.module.ingame.entities.character.HeroIngame;
import com.bamisu.log.gameserver.module.ingame.entities.character.SageIngame;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.FightingManager;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Room;

/**
 * Create by Popeye on 5:48 PM, 1/14/2020
 */
public class HumanPlayer extends BasePlayer {
    public String uid;

    public HumanPlayer(Room room, String uid, boolean havePuzzleBoard) {
        super(room, havePuzzleBoard);
        setPlayerType(EPlayerType.HUMAN);
        this.uid = uid;
        buildTeam();
    }

    @Override
    public void buildTeam() {
        switch (getFightingManager().type){
            case PvM:
                //hero
                String jsonData = (String) room.getProperty(Params.PLAYER_TEAM);
                HeroPackage heroPackage = Utils.fromJson(jsonData, HeroPackage.class);
                for(Hero hero : heroPackage.heroList){
                    if(hero != null){
                        team.add(new TeamSlot(new HeroIngame(this, hero)));
                    }else {
                        team.add(new TeamSlot(this));
                    }
                }

                //sage
                String jsonDataSage = (String) room.getProperty(Params.SAGE);
                Sage sage = Utils.fromJson(jsonDataSage, Sage.class);
                this.sage = new SageIngame(this, sage);

                //celestial
                String jsonDataCelestial = (String) room.getProperty(Params.CELESTIAL);
                Celestial celestial = Utils.fromJson(jsonDataCelestial, Celestial.class);
                this.celestial = new CelestialIngame(this, celestial);
                //Turn off celestial
                this.celestial = null;
                break;
        }
    }
}
