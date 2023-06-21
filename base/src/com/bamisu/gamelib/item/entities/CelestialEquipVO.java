package com.bamisu.gamelib.item.entities;

import java.util.ArrayList;
import java.util.List;

public class CelestialEquipVO {
    public String id;
    public short star;
    public String name;
    public List<AttributeVO> listAttr = new ArrayList<>();
    public String hash;
    public int count;
}
