package com.bamisu.log.gameserver.module.guild.cmd.rec;

import com.bamisu.gamelib.base.data.BaseCmd;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecCreateGuild extends BaseCmd {

    public String idPattern;
    public String idSymbol;
    public String name;
    public String description;
    public String verify;
    public String power;
    public String language;

    public RecCreateGuild(ISFSObject data) {
        super(data);
        unpackData();
    }

    @Override
    public void unpackData() {
        idPattern = data.getUtfString(Params.GUILD_PATTERN);
        idSymbol = data.getUtfString(Params.GUILD_SYMBOL);
        name = data.getUtfString(Params.NAME);
        description = data.getUtfString(Params.DESCRIPTION);
        verify = data.getUtfString(Params.VERIFICATION);
        power = data.getUtfString(Params.POWER);
        language = data.getUtfString(Params.LANGUAGE);
    }
}
