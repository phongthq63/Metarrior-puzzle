package com.bamisu.log.gameserver.module.celestial.cmd.send;

import com.bamisu.log.gameserver.datamodel.celestial.UserCelestialModel;
import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.entities.CMDUtilsServer;
import com.bamisu.log.gameserver.module.celestial.CelestialManager;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.SFSArray;

import java.util.Collection;
import java.util.List;

public class SendLoadSceneGetListCelestial extends BaseMsg {

    public UserCelestialModel userCelestialModel;
    public String use;
    public List<String> unlock;
    public Zone zone;
    public Collection<SkillInfo> skills;

    public SendLoadSceneGetListCelestial() {
        super(CMD.CMD_LOAD_SCENE_GET_LIST_CELESTIAL);
    }

    public SendLoadSceneGetListCelestial(short errorCode) {
        super(CMD.CMD_LOAD_SCENE_GET_LIST_CELESTIAL, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.USE, use);
        data.putUtfStringArray(Params.HAVE_ACTIVE, unlock);
        data.putShort(Params.ModuleChracter.LEVEL, userCelestialModel.readLevelCelestial(zone));
        data.putSFSObject(Params.ModuleHero.ATTRIBUTE,
                CMDUtilsServer.statsCelestialToSFSObject(
                        CelestialManager.getInstance().getStatsCelestial(use, userCelestialModel, zone)));
        data.putSFSArray(Params.SKILLS, SFSArray.newFromJsonData(Utils.toJson(skills)));
    }
}
