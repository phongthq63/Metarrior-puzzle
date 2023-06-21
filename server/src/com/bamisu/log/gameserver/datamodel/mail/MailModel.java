package com.bamisu.log.gameserver.datamodel.mail;

import com.bamisu.log.gameserver.datamodel.mail.config.InitMailVO;
import com.bamisu.log.gameserver.datamodel.mail.config.MailConfig;
import com.bamisu.log.gameserver.module.mail.MailManager;
import com.bamisu.log.gameserver.module.mail.entities.MailVO;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.model.DataModel;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class MailModel extends DataModel {
    public long uid;
    public List<MailVO> listMail = new ArrayList<>();
    public List<String> listIdMail = new ArrayList<>();

    public MailModel(long uId) {
        this.uid = uId;
        init();
    }

    private void init() {
        try {
            for (InitMailVO initMailVO : MailConfig.getInstance().initMails) {
                if (Utils.getTimestampInSecond() > initMailVO.fromTime && Utils.getTimestampInSecond() < initMailVO.toTime) {
                    listMail.add(new MailVO(initMailVO.title, initMailVO.content, initMailVO.gifts));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (int i = 0; i< 5; i++){
//            MailVO mailVO = new MailVO();
//            mailVO.idMail = Utils.genMailHash();
//            mailVO.statusReceive = EMailDefine.UNREAD.getStatus();
//            mailVO.statusRead = EMailDefine.UNREAD.getStatus();
//            mailVO.content = LanguageUtils.toTemplate("1000", Arrays.asList(123,123));
//            mailVO.time = Utils.timeNowString();
//            mailVO.title = "1001";
//            ResourcePackage resource = new ResourcePackage("MON1000",100);
//            List<ResourcePackage> list = new ArrayList<>();
//            list.add(resource);
//            mailVO.listGift = list;
//            this.listMail.add(mailVO);
//        }

    }

    public MailModel() {

    }


    public boolean clear(Zone zone) {
        listMail.clear();
        return saveToDB(zone);
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

    public static MailModel copyFromDBtoObject(long uId, Zone zone) {
        MailModel pInfo = null;
        try {
            String str = (String) getModel(String.valueOf(uId), MailModel.class, zone);
            if (str != null) {
                pInfo = Utils.fromJson(str, MailModel.class);
                if (pInfo != null) {
                }
            }
        } catch (DataControllerException e) {
        }
        if (pInfo == null) {
            pInfo = new MailModel(uId);
            pInfo.saveToDB(zone);
        }
        return pInfo;
    }

    public static MailModel create(long uId, Zone zone) {
        MailModel d = new MailModel(uId);
        if (d.saveToDB(zone)) {
            return d;
        }
        return null;
    }
}
