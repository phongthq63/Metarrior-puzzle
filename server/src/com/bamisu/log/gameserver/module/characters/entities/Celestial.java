package com.bamisu.log.gameserver.module.characters.entities;

import com.bamisu.log.gameserver.datamodel.celestial.CelestialSkillV5Model;
import com.bamisu.log.gameserver.datamodel.celestial.UserCelestialModel;
import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.celestial.CelestialManager;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class Celestial implements ICharacter {
    public int characterType = ECharacterType.Celestial.getType();
    public Stats stats;
    public String id;
    public int level;
    public int power;
    public List<SkillInfo> skillInfos = new ArrayList<>();

    public static Celestial createCelestial(Zone zone, long uid) {
        Celestial celestial = new Celestial();
        UserCelestialModel userCelestialModel = CelestialManager.getInstance().getUserCelestialModel(zone, uid);
        celestial.id = userCelestialModel.readIdCelestial();
        celestial.stats = CelestialManager.getInstance().getStatsCelestial(userCelestialModel.readIdCelestial(), userCelestialModel, zone);
        celestial.level = userCelestialModel.readLevelCelestial(zone);
        celestial.power = CelestialManager.getInstance().getPower(celestial.stats);

        CelestialSkillV5Model celestialSkillModel = CelestialSkillV5Model.copyFromDBtoObject(zone, uid, celestial.id);
        celestial.skillInfos = new ArrayList<>(celestialSkillModel.skills);
        return celestial;
    }

    /**
     * Luc chien Hero
     *
     * @param stats
     * @return
     */
    public int readPowerMage(Stats stats, CelestialManager manager) {
        return power;
    }

    public Stats readStats() {
        return stats;
    }

    @Override
    public String readID() {
        return id;
    }

    @Override
    public Object getSkill() {
        return skillInfos;
    }

    @Override
    public int readLevel() {
        return level;
    }

    @Override
    public int readStar() {
        return 0;
    }

    @Override
    public String readElement() {
        return "";
    }

    @Override
    public String readKingdom() {
        return "";
    }

    @Override
    public int readCharacterType() {
        return characterType;
    }

    @Override
    public float readHP() {
        return 0;
    }

    @Override
    public float readSTR() {
        return 0;
    }

    @Override
    public float readINT() {
        return 0;
    }

    @Override
    public float readATK() {
        return readStats().readAttack();
    }

    @Override
    public float readDEX() {
        return 0;
    }

    @Override
    public float readARM() {
        return 0;
    }

    @Override
    public float readMR() {
        return 0;
    }

    @Override
    public float readDEF() {
        return 0;
    }

    @Override
    public float readAGI() {
        return 0;
    }

    @Override
    public float readCRIT() {
        return readStats().readCrit();
    }

    @Override
    public float readCRITBONUS() {
        return readStats().readCritDmg();
    }

    @Override
    public float readAPEN() {
        return readStats().readDefensePenetration();
    }

    @Override
    public float readMPEN() {
        return readStats().readDefensePenetration();
    }

    @Override
    public float readDPEN() {
        return readStats().readDefensePenetration();
    }

    @Override
    public float readTEN() {
        return 0;
    }

    @Override
    public float readELU() {
        return 0;
    }

    @Override
    public int getLethal() {
        return 0;
    }
}
