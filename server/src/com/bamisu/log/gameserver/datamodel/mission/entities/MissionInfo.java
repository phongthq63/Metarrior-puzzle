package com.bamisu.log.gameserver.datamodel.mission.entities;

import com.bamisu.log.gameserver.module.bag.BagManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.bamisu.log.gameserver.module.campaign.config.BattleBackgroundConfigManager;
import com.bamisu.log.gameserver.module.campaign.entities.TeamUtils;
import com.bamisu.log.gameserver.module.campaign.config.entities.MonsterOnTeam;
import com.bamisu.log.gameserver.module.hunt.config.entities.SlotRewardVO;
import com.bamisu.log.gameserver.module.mission.MissionManager;
import com.bamisu.log.gameserver.module.mission.defind.EMissionStatus;
import com.bamisu.log.gameserver.module.mission.config.entities.*;
import com.bamisu.gamelib.entities.LIZRandom;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.RandomObj;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class MissionInfo {
    public String hash;
    public String id;
    public String status;
    public int redo;
    public boolean isWin;
    public String description;


    public static MissionInfo create(String id){
        MissionInfo mission = new MissionInfo();
        mission.hash = Utils.genMissiongHash();
        mission.id = id;
        mission.status = EMissionStatus.DOING.getId();
        mission.redo = 0;
        mission.isWin = false;
        mission.description = mission.genMissionDescription();

        return mission;
    }

    public static MissionInfo create(int level) {
        MissionInfo mission = new MissionInfo();
        mission.hash = Utils.genMissiongHash();
        mission.id = mission.genMissionId(level);
        mission.status = EMissionStatus.DOING.getId();
        mission.redo = 0;
        mission.isWin = false;
        mission.description = mission.genMissionDescription();

        return mission;
    }


    /*----------------------------------------------------------------------------------------------------------------*/
    /*----------------------------------------------------------------------------------------------------------------*/
    /**
     * Gen ra star nhiem vu
     * @param level
     * @return
     */
    private String genMissionId(int level){
        LIZRandom rd = MissionManager.getInstance().getMissionRate(level);
        return String.valueOf(rd.next().value);
    }


    /**
     * Gen discription
     */
    private String genMissionDescription(){
        List<String> names = MissionManager.getInstance().getMissionNameConfig();
        return names.get(Utils.randomInRange(0, names.size() - 1));
    }



    /*----------------------------------------------------------------------------------------------------------------*/
    public boolean updateStatusMission(EMissionStatus missionStatus, boolean isWin){
        this.status = missionStatus.getId();
        this.isWin = isWin;
        return true;
    }

    public List<ResourcePackage> readReward(){
        MissionVO cf = MissionManager.getInstance().getMissionConfig(id);
        if(cf == null) return new ArrayList<>();
        return cf.reward;
    }

    public int readStar(){
        MissionVO cf = MissionManager.getInstance().getMissionConfig(id);
        if(cf == null) return 0;
        return cf.star;
    }

    public int readTimeLimit(){
        MissionVO cf = MissionManager.getInstance().getMissionConfig(id);
        if(cf == null) return 0;
        return cf.win.time;
    }

    public int readTargetPoint(){
        MissionVO cf = MissionManager.getInstance().getMissionConfig(id);
        if(cf == null) return 0;
        return cf.win.target;
    }

    public short readCountCanDo(){
        return (short) (MissionManager.getInstance().getCountRedoMission() - redo);
    }
}
