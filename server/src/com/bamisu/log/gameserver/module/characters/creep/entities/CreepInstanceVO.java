package com.bamisu.log.gameserver.module.characters.creep.entities;

public class CreepInstanceVO {
    public String id;
    public short level;
    public short star;
    public String kingdom;
    public String element;

    public static CreepInstanceVO createCreepInstanceVO(String id, int level, int star, String kingdom, String element){
        CreepInstanceVO creepInstanceVO = new CreepInstanceVO();
        creepInstanceVO.id = id;
        creepInstanceVO.level = (short) level;
        creepInstanceVO.star = (short) star;
        creepInstanceVO.kingdom = kingdom;
        creepInstanceVO.element = element;

        return creepInstanceVO;
    }
}
