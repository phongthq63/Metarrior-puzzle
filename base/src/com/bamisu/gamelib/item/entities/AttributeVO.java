package com.bamisu.gamelib.item.entities;

public class AttributeVO {
    public int attr;
    public float param;
    public int type;

    public static AttributeVO create(AttributeVO attributeVO) {
        AttributeVO attributeCf = new AttributeVO();
        attributeCf.attr = attributeVO.attr;
        attributeCf.param = attributeVO.param;
        attributeCf.type = attributeVO.type;

        return attributeCf;
    }
}
