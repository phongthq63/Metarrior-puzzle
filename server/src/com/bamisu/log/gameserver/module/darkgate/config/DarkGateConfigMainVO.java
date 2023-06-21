package com.bamisu.log.gameserver.module.darkgate.config;

import java.util.List;

/**
 * Create by Popeye on 10:30 AM, 11/12/2020
 */
public class DarkGateConfigMainVO {
    public List<DarkGateEventVO> events;
    public String option;

    public DarkGateConfigMainVO() {
    }

    public DarkGateConfigMainVO(List<DarkGateEventVO> events) {
        this.events = events;
    }
}
