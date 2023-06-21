package com.bamisu.gamelib.item.entities;

import com.bamisu.gamelib.utils.Utils;

public class StoneDataVO {
    public String id;
    public String hash;
    public int level;
    public int count = 1;

    public StoneDataVO(){}

    public StoneDataVO(StoneVO stoneVO){
        this.id = stoneVO.id;
        this.hash = stoneVO.hash;
        this.level = stoneVO.level;
        this.count = 1;
    }

    public static StoneDataVO create(String id, int level){
        StoneDataVO stoneDataNew = new StoneDataVO();
        stoneDataNew.id = id;
        stoneDataNew.hash = Utils.genStoneHash();
        stoneDataNew.level = level;
        stoneDataNew.count = 1;

        return stoneDataNew;
    }
    public static StoneDataVO create(String id, int level, int count){
        StoneDataVO stoneDataNew = new StoneDataVO();
        stoneDataNew.id = id;
        stoneDataNew.hash = Utils.genStoneHash();
        stoneDataNew.level = level;
        stoneDataNew.count = count;

        return stoneDataNew;
    }
    public static StoneDataVO create(StoneDataVO stoneData){
        if(stoneData == null) return null;

        StoneDataVO stoneDataNew = new StoneDataVO();
        stoneDataNew.id = stoneData.id;
        stoneDataNew.hash = stoneData.hash;
        stoneDataNew.level = stoneData.level;
        stoneDataNew.count = stoneData.count;

        return stoneDataNew;
    }
    public static StoneDataVO create(StoneDataVO stoneData, int count){
        if(stoneData == null) return null;

        StoneDataVO stoneDataNew = new StoneDataVO();
        stoneDataNew.id = stoneData.id;
        stoneDataNew.hash = stoneData.hash;
        stoneDataNew.level = stoneData.level;
        stoneDataNew.count = count;

        return stoneDataNew;
    }
    public static StoneDataVO create1(StoneDataVO stoneData){
        if(stoneData == null) return null;

        StoneDataVO stoneDataNew = new StoneDataVO();
        stoneDataNew.id = stoneData.id;
        stoneDataNew.hash = stoneData.hash;
        stoneDataNew.level = stoneData.level;
        stoneDataNew.count = 1;

        return stoneDataNew;
    }
}
