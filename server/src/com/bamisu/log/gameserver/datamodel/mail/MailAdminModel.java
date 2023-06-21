package com.bamisu.log.gameserver.datamodel.mail;

import com.bamisu.log.gameserver.module.mail.entities.MailVO;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class MailAdminModel extends DataModel {
    public static long id = 0;
    public List<MailVO> listMail = new ArrayList<>();
    public MailAdminModel(){}

    public final boolean saveToDB(Zone zone) {
        try {
            saveModel(String.valueOf(id), zone);
            return true;
        } catch (DataControllerException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static MailAdminModel copyFromDBtoObject(Zone zone) {
        MailAdminModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(id), MailAdminModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, MailAdminModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {
        }
        if (pInfo == null) {
            pInfo = new MailAdminModel();
            pInfo.saveToDB(zone);
        }
        return pInfo;
    }
}
