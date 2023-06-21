package com.bamisu.gamelib.entities;

import java.util.List;

public class LevelUserConfig {
    public List<LevelSageVO> listLevel;

    public LevelUserConfig() {
    }

    public LevelUserConfig(List<LevelSageVO> listLevel) {
        this.listLevel = listLevel;
    }
}
