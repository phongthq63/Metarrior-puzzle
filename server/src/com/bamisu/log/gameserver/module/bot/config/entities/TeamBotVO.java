package com.bamisu.log.gameserver.module.bot.config.entities;

import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.hero.HeroManager;

import java.util.List;

public class TeamBotVO {
    public List<HeroModel> team;
    public int power;

    public static TeamBotVO create(List<HeroModel> team) {
        TeamBotVO teamBot = new TeamBotVO();
        teamBot.team = team;
        teamBot.power = HeroManager.getInstance().getNormalPower(team);

        return teamBot;
    }
}
