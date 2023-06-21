package com.bamisu.log.gameserver.module.guild.entities;

public class SettingGuildVO {
    public String id;
    public String param;

    public static SettingGuildVO create(String id, String param) {
        SettingGuildVO settingGuildVO = new SettingGuildVO();
        settingGuildVO.id = id;
        settingGuildVO.param = param;

        return settingGuildVO;
    }
}
