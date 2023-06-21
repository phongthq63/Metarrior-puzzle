package com.bamisu.log.gameserver.module.bot;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.bot.config.TeamBotConfig;
import com.bamisu.log.gameserver.module.bot.config.entities.TeamBotVO;

import java.util.ArrayList;
import java.util.List;

public class TeamBotManager {

    private TeamBotConfig teamBotConfig;


    private static TeamBotManager ourInstance = new TeamBotManager();

    public static TeamBotManager getInstance() {
        return ourInstance;
    }

    private TeamBotManager() {
        loadConfig();
    }

    private void loadConfig(){
        teamBotConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Bot.FILE_PATH_CONFIG_TEAM_BOT), TeamBotConfig.class);
    }


    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    public List<TeamBotVO> getListTeamBot(int power){
        List<TeamBotVO> listGet = new ArrayList<>();

        for(TeamBotVO teamBot : teamBotConfig.list){
            if(0.9 * power <= teamBot.power && teamBot.power <= 1.1 * power){
                listGet.add(teamBot);
            }
        }

        return listGet;
    }









}
