package com.bamisu.gamelib.entities;

import java.util.List;
import java.util.Map;

public class WithdrawFeeConfig {
    private List<FeeConfig> config;

    public WithdrawFeeConfig() {}

    public List<FeeConfig> getConfig() {
        return config;
    }

    public void setConfig(List<FeeConfig> config) {
        this.config = config;
    }
}
