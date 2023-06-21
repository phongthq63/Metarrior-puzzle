package com.bamisu.gamelib.item.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StoneVO {
    public String id;
    public String hash;
    public String name;
    public int level;
    public int type;
    public int count;
    public List<AttributeVO> listAttr;
    public int maxLevel;
    public String represent;

    public StoneVO(){}

    public StoneVO(StoneDataVO stoneDataVO, StoneConfigVO stoneConfigVO, StoneLevelConfigVO stoneLevelConfigVO){
        //Config
        this.id = stoneConfigVO.id;
        this.name = stoneConfigVO.name;
        this.type = stoneConfigVO.type;

        //Data
        this.hash = stoneDataVO.hash;
        this.level = stoneDataVO.level;
        this.count = stoneDataVO.count;
        this.represent = stoneConfigVO.represent;

        this.maxLevel = stoneLevelConfigVO.maxLevel;

        //Config Attribute
        for (StoneLevelVO vo: stoneLevelConfigVO.listLevel){
            if (vo.level == stoneDataVO.level){
                this.listAttr = vo.listAttr;
                break;
            }
        }
    }

    public StoneVO(StoneConfigVO stoneConfigVO, StoneLevelConfigVO stoneLevelConfigVO, StoneLevelVO stoneLevelVO){
        //Config
        this.id = stoneConfigVO.id;
        this.name = stoneConfigVO.name;
        this.type = stoneConfigVO.type;

        //Data
        this.level = stoneLevelVO.level;

        //Config Attribute
        this.listAttr = stoneLevelVO.listAttr;
    }

    public static StoneVO create(StoneVO stoneVO){
        if(stoneVO == null) return null;

        StoneVO stoneCf = new StoneVO();
        stoneCf.id = stoneVO.id;
        stoneCf.level = stoneVO.level;
        stoneCf.hash = stoneVO.hash;
        stoneCf.type = stoneVO.type;
        stoneCf.listAttr = stoneVO.listAttr.stream().map(AttributeVO::create).collect(Collectors.toList());
        stoneCf.count = stoneVO.count;
        stoneCf.name = stoneVO.name;
        stoneCf.maxLevel = stoneVO.maxLevel;
        stoneCf.represent = stoneVO.represent;

        return stoneCf;
    }
}
