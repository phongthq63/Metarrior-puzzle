package com.bamisu.gamelib.item.entities;

public class FragmentVO {
    public String id;
    public int amount;

    public FragmentVO(){}

    public FragmentVO(String id ,int amount){
        this.id = id;
        this.amount = amount;
    }

    public FragmentVO(FragmentConfigVO vo){
        this.id = vo.id;
        this.amount = 0;
    }
}
