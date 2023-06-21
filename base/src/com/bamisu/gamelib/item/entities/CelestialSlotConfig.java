package com.bamisu.gamelib.item.entities;

import java.util.List;

public class CelestialSlotConfig {
    public int maxCelestialSlot;
    public List<CelestialSlotVO> listCelestialSlot;

    public CelestialSlotConfig(){}

    public CelestialSlotConfig(int maxCelestialSlot, List<CelestialSlotVO> listCelestialSlot){
        this.maxCelestialSlot = maxCelestialSlot;
        this.listCelestialSlot = listCelestialSlot;
    }
}
