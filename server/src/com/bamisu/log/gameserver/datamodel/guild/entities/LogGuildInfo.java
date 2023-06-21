package com.bamisu.log.gameserver.datamodel.guild.entities;

import com.bamisu.gamelib.utils.Utils;

import java.util.List;

public class LogGuildInfo {
    public String id;
    public List<String> param;
    public int time;

    public static LogGuildInfo createLogGuildInfo(String id, List<String> param) {
        LogGuildInfo log = new LogGuildInfo();
        log.id = id;
        log.param = param;
        log.time = Utils.getTimestampInSecond();

        return log;
    }
}
