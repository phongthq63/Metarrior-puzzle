package com.bamisu.log.gameserver.module.guild.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.log.gameserver.module.guild.define.EGuildSetting;
import com.bamisu.log.gameserver.module.guild.entities.SettingGuildVO;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.List;

public class RecSettingGuild extends BaseCmd {

    public List<SettingGuildVO> listSetting;

    private String pattern = "";
    private String symbol = "";
    private String name = "";
    private String description = "";
    private String verify = "";
    private String power = "";
    private String language = "";



    public RecSettingGuild(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        listSetting = new ArrayList<>();

        if(data.containsKey(Params.GUILD_PATTERN) && data.containsKey(Params.GUILD_SYMBOL)){
            pattern = data.getUtfString(Params.GUILD_PATTERN);
            symbol = data.getUtfString(Params.GUILD_SYMBOL);

            if(!pattern.isEmpty() && !symbol.isEmpty()) listSetting.add(SettingGuildVO.create(EGuildSetting.AVATAR.getId(), pattern + ServerConstant.SEPARATER + symbol));
        }
        if(data.containsKey(Params.DESCRIPTION)){
            description = data.getUtfString(Params.DESCRIPTION);

            if(!description.isEmpty()) listSetting.add(SettingGuildVO.create(EGuildSetting.NOTICE.getId(), description));
        }
        if(data.containsKey(Params.VERIFICATION)){
            verify = data.getUtfString(Params.VERIFICATION);

            if(!verify.isEmpty()) listSetting.add(SettingGuildVO.create(EGuildSetting.VERIFICATION.getId(), verify));
        }
        if(data.containsKey(Params.POWER)){
            power = data.getUtfString(Params.POWER);

            if(!power.isEmpty()) listSetting.add(SettingGuildVO.create(EGuildSetting.POWER_REQUEST.getId(), power));
        }
        if(data.containsKey(Params.LANGUAGE)){
            language = data.getUtfString(Params.LANGUAGE);

            if(!language.isEmpty()) listSetting.add(SettingGuildVO.create(EGuildSetting.LANGUAGE.getId(), language));
        }
    }
}
