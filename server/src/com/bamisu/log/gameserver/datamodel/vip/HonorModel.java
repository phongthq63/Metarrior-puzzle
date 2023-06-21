package com.bamisu.log.gameserver.datamodel.vip;

import com.bamisu.gamelib.entities.EVip;
import com.bamisu.gamelib.entities.VipData;
import com.bamisu.log.gameserver.module.WoL.defines.WoLConquerStatus;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.log.gameserver.module.vip.defines.EGiftVip;
import com.bamisu.log.gameserver.module.vip.entities.*;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class HonorModel extends DataModel {
    public long uId;
    public int levelHonor = 0;
    public List<HonorDataVO> listHonor = new ArrayList<>();
    public long time;
    public List<TimeVipVO> timeVip = new ArrayList<>();

    public HonorModel() {}

    public HonorModel(long uId) {
        this.uId = uId;
        initTimeVip();
        init();
    }

    public void initTimeVip(){
        if (this.timeVip == null || this.timeVip.size() == 0){
            this.timeVip = new ArrayList<>();
            this.timeVip.add(new TimeVipVO(EVip.ARCHMAGE, 0));
            this.timeVip.add(new TimeVipVO(EVip.PROTECTOR, 0));
        }
    }

    private void init() {



        List<HonorVO> list = VipManager.getInstance().getHonorConfig();
        for (HonorVO honorVO: list){
            //beginning
            if (honorVO.id == 0){
                HonorDataVO honorDataVO = new HonorDataVO(honorVO.id, WoLConquerStatus.CAN_RECEIVE.getStatus());
                this.listHonor.add(honorDataVO);
            }else{
                HonorDataVO honorDataVO = new HonorDataVO(honorVO.id, WoLConquerStatus.INCOMPLETE.getStatus());
                this.listHonor.add(honorDataVO);
            }

        }
    }


    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(this.uId), zone);
            return true;

        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static HonorModel copyFromDBtoObject(long uId, Zone zone) {
        return copyFromDBtoObject(String.valueOf(uId), zone);
    }

    public static HonorModel copyFromDBtoObject(String uId, Zone zone) {
        HonorModel pInfo = null;
        try {
            String str = (String) getModel(uId, HonorModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, HonorModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {

        }
        if (pInfo == null) {
            pInfo = new HonorModel(Long.parseLong(uId));
            pInfo.saveToDB(zone);
        }

        //them time vip
        if(pInfo.timeVip.isEmpty()){
            pInfo.initTimeVip();
        }
        return pInfo;
    }

    public static HonorModel create(long uId, Zone zone) {
        HonorModel d = new HonorModel(uId);
        if (d.saveToDB(zone)) {
            return d;
        }
        return null;
    }
}
