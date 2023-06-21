package com.bamisu.log.gameserver.module.characters.celestial.entities;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.log.gameserver.module.characters.entities.ICharacterVO;

import java.util.List;

public class CelestialVO implements ICharacterVO {
    public String id;
    public String name;
    public int atk;
    public int crit;
    public int critDmg;
    public int pierce;
    public List<String> skill;
    public List<ResourcePackage> unlock;

    @Override
    public String getID() {
        return id;
    }

    @Override
    public int readStar() {
        return 0;
    }

    @Override
    public String readkingdom() {
        return null;
    }

    @Override
    public String readelement() {
        return null;
    }
}
