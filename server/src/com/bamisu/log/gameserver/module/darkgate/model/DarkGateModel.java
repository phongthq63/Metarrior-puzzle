package com.bamisu.log.gameserver.module.darkgate.model;

import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.darkgate.entities.EDarkGateEvent;
import com.bamisu.log.gameserver.module.darkgate.model.entities.ActiveEventVO;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 11:14 AM, 11/12/2020
 */
public class DarkGateModel extends DataModel {
    private static final String key = "0";
    public List<ActiveEventVO> activeEvents = new ArrayList<>();
    public List<ActiveEventVO> willActiveEvents = new ArrayList<>();

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(key, zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static DarkGateModel copyFromDBtoObject(Zone zone) {
        DarkGateModel model = null;
        try {
            String str = (String) getModel(key, DarkGateModel.class, zone);
            if (str != null) {
                model = Utils.fromJson(str, DarkGateModel.class);
                if (model != null) {
                }
            }
        } catch (DataControllerException e) {
            e.printStackTrace();
        }

        if(model == null){
            model = new DarkGateModel();
            model.saveToDB(zone);
        }
        return model;
    }


    /**
     * check xem có sự kiện này đang diễn ra không
     * @param eventID
     * @return
     */
    public boolean containsEventID(int eventID) {
        for(ActiveEventVO activeEventVO : activeEvents){
            if(activeEventVO.id == eventID) return true;
        }

        return false;
    }

    /**
     * check xem có sự kiện này đang diễn ra không
     * @param eventID
     * @return
     */
    public boolean containsEventIDWillStart(int eventID) {
        for(ActiveEventVO activeEventVO : willActiveEvents){
            if(activeEventVO.id == eventID) return true;
        }

        return false;
    }

    /**
     * lấy 1 sự kiện đang diễn ra
     * @param eDarkGateEvent
     * @return
     */
    public ActiveEventVO getActiveEvent(EDarkGateEvent eDarkGateEvent){
        for(ActiveEventVO activeEventVO : activeEvents){
            if(activeEventVO.id == eDarkGateEvent.id) return activeEventVO;
        }

        return null;
    }
}
