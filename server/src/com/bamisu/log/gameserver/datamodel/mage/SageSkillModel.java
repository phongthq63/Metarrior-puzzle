package com.bamisu.log.gameserver.datamodel.mage;

import com.bamisu.gamelib.skill.config.entities.SkillInfo;
import com.bamisu.gamelib.skill.SkillConfigManager;
import com.bamisu.gamelib.skill.config.entities.SageSkillVO;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Create by Popeye on 4:55 PM, 3/6/2020
 */
public class SageSkillModel extends DataModel {
    public long uid;
    public List<SkillInfo> skills;
    public Collection<SkillInfo> currentSkill = Arrays.asList(); // skill được chọn để dùng
    public SkillInfo currentUltil = new SkillInfo("SS0001", 1); // skill được chọn để dùng

    public SageSkillModel() {
    }

    private SageSkillModel(long uid) {
        this.uid = uid;
        initSkill();
    }

    public void initSkill(){
        skills = new ArrayList<>();
        skills.add(new SkillInfo("SS0001", 1));
        currentSkill = new ArrayList<>();
        currentUltil = new SkillInfo("SS0001", 1);
    }

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uid), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static SageSkillModel copyFromDBtoObject(String uId, Zone zone) {
        return copyFromDBtoObject(Long.parseLong(uId), zone);
    }

    public static SageSkillModel copyFromDBtoObject(long uId, Zone zone) {
        SageSkillModel model = null;
        try {
            String str = (String) getModel(String.valueOf(uId), SageSkillModel.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, SageSkillModel.class);
                if (model != null) {
//                    pInfo.writeLog();
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }
        if (model == null) {
            model = new SageSkillModel(uId);
        }

        return model;
    }

    public int readUsedSkillPoint() {
        int s = 0;
        for (SkillInfo sageSkillInfo : skills) {
            s += sageSkillInfo.level;
        }

        return s;
    }

    public SkillInfo readLearnedSkill(String id) {
        for (SkillInfo sageSkillInfo : skills) {
            if (sageSkillInfo.id.equalsIgnoreCase(id)) return sageSkillInfo;
        }

        return null;
    }

    public int readLastColum() {
        int lastColum = -1;
        for (SkillInfo sageSkillInfo : skills) {
            SageSkillVO vo = SkillConfigManager.getInstance().getSageSkill(sageSkillInfo.id);
            if (vo.column > lastColum) {
                lastColum = vo.column;
            }
        }

        return lastColum;
    }

    public void resetColum(Zone zone) {
        List<SkillInfo> listToRemove = new ArrayList<>();
        for (SkillInfo sageSkillInfo : skills) {
            SageSkillVO vo = SkillConfigManager.getInstance().getSageSkill(sageSkillInfo.id);
            if (vo.column != 1 && vo.column == readLastColum()) {
                listToRemove.add(sageSkillInfo);
            }
        }

        if (!listToRemove.isEmpty()) {
            skills.removeAll(listToRemove);
        }

        //remove Ultimate
        for(SkillInfo skillInfo : listToRemove){
            if(skillInfo.id.equalsIgnoreCase(currentUltil.id)){
                currentUltil = new SkillInfo("SS0001", 1);
            }
        }

        //remove current skill
        List<SkillInfo> listToRemoveCurentSkill = new ArrayList<>();
        for(SkillInfo skillInfo : listToRemove){
            for(SkillInfo skillInfoCurrent: currentSkill){
                if(skillInfo.id.equalsIgnoreCase(skillInfoCurrent.id)){
                    listToRemoveCurentSkill.add(skillInfoCurrent);
                }
            }
        }
        if(listToRemoveCurentSkill != null){
            currentSkill.removeAll(listToRemoveCurentSkill);
        }

        saveToDB(zone);
    }

    public void updateCurrentSkill(Zone zone, Collection<String> updateSageSkill) {
        currentSkill.clear();
        for(String sid : updateSageSkill){
            for(SkillInfo skillInfo : skills){
                if(skillInfo.id.equalsIgnoreCase(sid)){
                    currentSkill.add(new SkillInfo(sid, skillInfo.level));
                    break;
                }
            }
        }

        saveToDB(zone);
    }

    public SkillInfo readCurrentUltil() {
        SkillInfo maxUtilmate = null;
        for(SkillInfo skillInfo : skills){
            if(SkillConfigManager.getInstance().getSageSkill(skillInfo.id).type.equalsIgnoreCase("Ultimate")){
                if(maxUtilmate == null){
                    maxUtilmate = skillInfo;
                }else {
                    if(SkillConfigManager.getInstance().getSageSkill(skillInfo.id).column > SkillConfigManager.getInstance().getSageSkill(maxUtilmate.id).column){
                        maxUtilmate = skillInfo;
                    }
                }
            }
        }

        return maxUtilmate;
    }
}
