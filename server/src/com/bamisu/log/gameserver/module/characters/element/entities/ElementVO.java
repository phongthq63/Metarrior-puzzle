package com.bamisu.log.gameserver.module.characters.element.entities;

import com.bamisu.gamelib.item.entities.MoneyPackageVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ElementVO {
    public String id;
    public String name;
    public List<MoneyPackageVO> cost;

    public static ElementVO createElement(String id, String name){
        ElementVO element = new ElementVO();
        element.id = id;
        element.name = name;

        return element;
    }

    public List<MoneyPackageVO> getCostUpdateElementDay(){
        return cost.parallelStream().map(obj -> new MoneyPackageVO(obj.id, -obj.amount)).collect(Collectors.toList());
    }
}
