package com.bamisu.log.gameserver.module.characters.clas.entities;

public class ClassVO {
    public String id;
    public String name;

    public static ClassVO createClass(String id, String name){
        ClassVO classVO = new ClassVO();
        classVO.id = id;
        classVO.name = name;

        return classVO;
    }
}
