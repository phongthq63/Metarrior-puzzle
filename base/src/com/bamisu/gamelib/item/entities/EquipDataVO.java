package com.bamisu.gamelib.item.entities;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.item.ItemManager;

import java.util.List;
import java.util.stream.Collectors;

public class EquipDataVO implements IItemData, IResourcePackage {
    public String id;
    public String hash;
    public int level;
    public int exp;
    public int expFis;
    public String hashHero;
    public int star;
    public int position;
    public List<StoneSlotVO> listSlotStone = ItemManager.getInstance().getStoneSlotConfig();
    public int count;


    public EquipDataVO(){}

    public EquipDataVO(EquipVO equipVO){
        this.id = equipVO.id;
        this.hash = equipVO.hash;
        this.level = equipVO.level;
        this.exp = equipVO.exp;
        this.hashHero = equipVO.hashHero;
        this.star = equipVO.star;
        this.position = equipVO.position;
        this.listSlotStone = equipVO.listSlotStone.stream().map(StoneSlotVO::create).collect(Collectors.toList());
        this.expFis = equipVO.expFis;
        this.count = equipVO.count;
    }

    public static EquipDataVO create(int position){
        EquipDataVO equipData = new EquipDataVO();
        equipData.id = null;
        equipData.position = position;

        return equipData;
    }

    public static EquipDataVO create(EquipDataVO equipData){
        if(equipData == null) return null;

        EquipDataVO equipNew = new EquipDataVO();
        equipNew.id = equipData.id;
        equipNew.hash = equipData.hash;
        equipNew.level = equipData.level;
        equipNew.exp = equipData.exp;
        equipNew.hashHero = equipData.hashHero;
        equipNew.star = equipData.star;
        equipNew.position = equipData.position;
        equipNew.listSlotStone = equipData.listSlotStone.stream().map(StoneSlotVO::create).collect(Collectors.toList());
        equipNew.expFis = equipData.expFis;
        equipNew.count = equipData.count;

        return equipNew;
    }
    public static EquipDataVO create(EquipDataVO equipData, int count){
        if(equipData == null) return null;

        EquipDataVO equipNew = new EquipDataVO();
        equipNew.id = equipData.id;
        equipNew.hash = equipData.hash;
        equipNew.level = equipData.level;
        equipNew.exp = equipData.exp;
        equipNew.hashHero = equipData.hashHero;
        equipNew.star = equipData.star;
        equipNew.position = equipData.position;
        equipNew.listSlotStone = equipData.listSlotStone.stream().map(StoneSlotVO::create).collect(Collectors.toList());
        equipNew.expFis = equipData.expFis;
        equipNew.count = count;

        return equipNew;
    }

    public static EquipDataVO create1(EquipDataVO equipData){
        if(equipData == null) return null;

        EquipDataVO equipNew = new EquipDataVO();
        equipNew.id = equipData.id;
        equipNew.hash = equipData.hash;
        equipNew.level = equipData.level;
        equipNew.exp = equipData.exp;
        equipNew.hashHero = equipData.hashHero;
        equipNew.star = equipData.star;
        equipNew.position = equipData.position;
        equipNew.listSlotStone = equipData.listSlotStone.stream().map(StoneSlotVO::create).collect(Collectors.toList());
        equipNew.expFis = equipData.expFis;
        equipNew.count = 1;

        return equipNew;
    }

    public List<StoneVO> readListStoneEquip(){
        return listSlotStone.stream().
                filter(StoneSlotVO::haveLock).
                map(obj -> obj.stoneVO).
                collect(Collectors.toList());
    }

    public boolean haveInBag(){
        return hashHero == null || hashHero.isEmpty();
    }

    public boolean haveStone(){
        boolean result = false;
        for(StoneSlotVO slot : listSlotStone){
            if(slot.haveLock() && slot.stoneVO != null) return true;
        }
        return result;
    }

    @Override
    public String readId() {
        return id;
    }

    @Override
    public String readHash() {
        return hash;
    }

    @Override
    public int readAmount() {
        return count;
    }

    @Override
    public int readStar() {
        return star;
    }

    @Override
    public int readLevel() {
        return level;
    }

    @Override
    public int readCount() {
        return count;
    }
}
