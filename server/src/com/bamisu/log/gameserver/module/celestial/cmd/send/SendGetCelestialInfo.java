package com.bamisu.log.gameserver.module.celestial.cmd.send;

import com.bamisu.log.gameserver.datamodel.celestial.UserCelestialModel;
import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.log.gameserver.entities.CMDUtilsServer;
import com.bamisu.log.gameserver.module.celestial.CelestialManager;
import com.bamisu.gamelib.base.data.BaseMsg;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.SFSArray;

import java.util.Collection;

public class SendGetCelestialInfo extends BaseMsg {

    public UserCelestialModel userCelestialModel;
    public String idCelestial;
    public Zone zone;
    public Collection<SkillInfo> skills;

    public SendGetCelestialInfo() {
        super(CMD.CMD_GET_CELESTIAL_INFO);
    }

    public SendGetCelestialInfo(short errorCode) {
        super(CMD.CMD_GET_CELESTIAL_INFO, errorCode);
    }

    @Override
    public void packData() {
        super.packData();
        if(isError())return;

        data.putUtfString(Params.ID, idCelestial);
        data.putShort(Params.ModuleChracter.LEVEL, userCelestialModel.readLevelCelestial(zone));
        data.putSFSObject(Params.ModuleHero.ATTRIBUTE,
                CMDUtilsServer.statsCelestialToSFSObject(
                        CelestialManager.getInstance().getStatsCelestial(idCelestial, userCelestialModel, zone)));
        data.putSFSArray(Params.SKILLS, SFSArray.newFromJsonData(Utils.toJson(skills)));
    }
}
