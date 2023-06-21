package com.bamisu.log.gameserver.datamodel.celestial;

import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.celestial.entities.CelestialVO;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Create by Popeye on 2:39 PM, 7/9/2020
 */
public class CelestialSkillV5Model extends DataModel {
    public long uid;
    public String cid;
    public Collection<SkillInfo> skills = new ArrayList<>();

    public CelestialSkillV5Model() {
    }

    public CelestialSkillV5Model(long uid, String cid) {
        this.uid = uid;
        this.cid = cid;
        CelestialVO celestialVO = CharactersConfigManager.getInstance().getCelestialConfig(cid);
        if(celestialVO != null){
            for(String skillID : celestialVO.skill){
                skills.add(new SkillInfo(skillID, 1));
            }
        }
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(uid + "_" + cid, zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static CelestialSkillV5Model copyFromDBtoObject(Zone zone, long uid, String cid) {
        CelestialSkillV5Model model = null;
        try {
            String str = (String) getModel(uid + "_" + cid, CelestialSkillV5Model.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, CelestialSkillV5Model.class);
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        if (model == null) {
            model = new CelestialSkillV5Model(uid, cid);
            model.saveToDB(zone);
        }

        return model;
    }
}
