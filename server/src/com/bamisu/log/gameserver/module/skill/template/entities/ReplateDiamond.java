package com.bamisu.log.gameserver.module.skill.template.entities;

import com.bamisu.log.gameserver.module.ingame.entities.Diamond;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 4:07 PM, 6/17/2020
 */
public class ReplateDiamond {
    public List<String> nonElement = new ArrayList<>();
    public String target = "";

    public List<Integer> getNonElement(){
        List<Integer> list = new ArrayList<>();
        for(String strElement :nonElement){
            list.add(Diamond.fromName(strElement).getValue());
        }
        return list;
    }
}
