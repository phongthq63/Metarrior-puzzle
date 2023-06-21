package com.bamisu.gamelib.item.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EquipVO {
    public String id;
    public String hash;
    public int level;
    public int position;
    public String name;
    public int star;
    public int exp;
    public String kingdom;
    public int maxLevel;
    public String hashHero;
    public int expNeed;
    public List<AttributeVO> listAttr = new ArrayList<>();
    public List<StoneSlotVO> listSlotStone = new ArrayList<>();
    public int expFis;
    public int count;
    public String profession;
    public String type;

    public EquipVO(){}

    public EquipVO(EquipDataVO equipDataVO, EquipConfigVO equipConfigVO, EquipLevelConfigVO equipLevelConfigVO, EquipLevelVO equipLevelVO){
        //In Data
        this.id = equipConfigVO.id;
        this.hash = equipDataVO.hash;
        this.hashHero = equipDataVO.hashHero;
        this.exp = equipDataVO.exp;
        this.listSlotStone = equipDataVO.listSlotStone;
        this.expFis = equipDataVO.expFis;

        //In Equip Config
        this.position = equipConfigVO.position;
        this.kingdom = equipConfigVO.kingdom;
        this.name = equipConfigVO.name;
        this.star = equipConfigVO.star;
        this.type = equipConfigVO.type;
        this.profession = equipConfigVO.profession;

        //In Level Equip Config
        this.level = equipLevelVO.level;
        this.maxLevel = equipLevelConfigVO.maxLevel;
        this.expNeed = equipLevelVO.expNeed;
        this.listAttr = equipLevelVO.listAttr;

        this.count = equipDataVO.count;
    }
}
