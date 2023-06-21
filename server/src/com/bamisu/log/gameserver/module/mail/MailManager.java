package com.bamisu.log.gameserver.module.mail;

import com.bamisu.gamelib.utils.UserUtils;
import com.bamisu.log.gameserver.module.vip.VipManager;
import com.bamisu.log.gameserver.ZoneExtension;
import com.bamisu.log.gameserver.datamodel.mail.MailAdminModel;
import com.bamisu.log.gameserver.datamodel.mail.MailModel;
import com.bamisu.log.gameserver.module.adventure.AdventureManager;
import com.bamisu.log.gameserver.module.bag.BagManager;
import com.bamisu.log.gameserver.module.mail.cmd.send.*;
import com.bamisu.log.gameserver.module.mail.define.EMailDefine;
import com.bamisu.log.gameserver.module.mail.entities.MailVO;
import com.bamisu.log.gameserver.module.mail.entities.TimeExpireMailConfig;
import com.bamisu.gamelib.base.model.UserModel;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;

import java.text.ParseException;;
import java.util.*;

public class MailManager {
    private static MailManager ourInstance = null;

    public static MailManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new MailManager();
        }
        return ourInstance;
    }

    public MailAdminModel getMailAdminModel(Zone zone) {
        return MailAdminModel.copyFromDBtoObject(zone);
//        return ((ZoneExtension)zone.getExtension()).getZoneCacheData().getMailAdminModelCache();
    }

    public MailManager() {
    }

    public int getTimeMail() {
        return 14 * 24 * 60 * 60;   //14 ngày
    }

    public MailModel getMailModel(long uid, Zone zone) {
        MailModel mailModel = MailModel.copyFromDBtoObject(uid, zone);
//        MailModel mailModel = ((ZoneExtension)(zone.getExtension())).getZoneCacheData().getMailModelCache(String.valueOf(uid));
        MailAdminModel mailAdminModel = getMailAdminModel(zone);
        updateMail(zone, mailAdminModel, mailModel);
        return mailModel;
    }

    public void readMail(MailHandler mailHandler, String idMail, long uid, User user) {
        MailModel mailModel = getMailModel(uid, user.getZone());
        for (MailVO mailVO : mailModel.listMail) {
            if (idMail.equals(mailVO.idMail)) {
                mailVO.statusRead = EMailDefine.READ.getStatus();
                SendReadMail send = new SendReadMail();
//                send.mailVO = mailVO;
                mailHandler.send(send, user);
                mailModel.saveToDB(user.getZone());
                return;
            }
        }
    }

    public void getListMail(MailHandler mailHandler, long uid, User user) {
        MailModel mailModel = getMailModel(uid, user.getZone());

        //Send Gift
        VipManager.getInstance().updateGiftFromMail(uid, user.getZone());
        SendGetListMail send = new SendGetListMail();
        send.listMail = mailModel.listMail;
        mailHandler.send(send, user);
    }

    public void collectAllMail(MailHandler mailHandler , User user, long uid) {
        MailModel mailModel = getMailModel(uid, user.getZone());
        Map<String, ResourcePackage> map = new HashMap<>();
        for (MailVO mailVO : mailModel.listMail) {
            mailVO.statusRead = EMailDefine.READ.getStatus();
            if (checkTimeExpire(mailVO.time) && mailVO.statusReceive == EMailDefine.UNREAD.getStatus()) {
                mailVO.statusReceive = EMailDefine.READ.getStatus();

                for (ResourcePackage vo : mailVO.listGift) {
                    if (map.get(vo.id) == null) {
                        map.put(vo.id, new ResourcePackage(vo));
                    } else {
                        map.get(vo.id).amount += vo.amount;
                    }
                }
            }
        }
        List<ResourcePackage> listResource = new ArrayList<>(map.values());

        if (BagManager.getInstance().addItemToDB(listResource, uid, user.getZone(), UserUtils.TransactionType.GIFT_MAIL)) {
            mailModel.saveToDB(user.getZone());
            SendCollectAllMail send = new SendCollectAllMail();
            mailHandler.send(send, user);
        } else {
            SendCollectAllMail send = new SendCollectAllMail(ServerConstant.ErrorCode.ERR_SYS);
            mailHandler.send(send, user);
        }
    }

    public void collectMail(MailHandler mailHandler, User user, long uid, String idMail) {
        MailModel mailModel = getMailModel(uid, user.getZone());
        for (MailVO mailVO : mailModel.listMail) {
            if (idMail.equals(mailVO.idMail)) {
                if (checkTimeExpire(mailVO.time) && mailVO.statusReceive == EMailDefine.UNREAD.getStatus()) {
                    mailVO.statusReceive = EMailDefine.READ.getStatus();
                    if (BagManager.getInstance().addItemToDB(mailVO.listGift, uid, user.getZone(), UserUtils.TransactionType.GIFT_MAIL)) {
                        mailModel.saveToDB(user.getZone());
                        SendCollectMail send = new SendCollectMail();
                        mailHandler.send(send, user);
                        return;
                    }
                }
            }
        }
        SendCollectMail send = new SendCollectMail(ServerConstant.ErrorCode.ERR_SYS);
        mailHandler.send(send, user);
    }

    public boolean checkTimeExpire(String time) {
        return getTimeExpire(time) < getTimeMail();
    }

    public int getTimeExpire(String dateBeforeString) {
        //Parsing the date
        int dateBeforeInt = Utils.getTimeSecondFromString(Utils.DATE_TIME_FORMAT, dateBeforeString);
        int dateAfterInt = Utils.getTimestampInSecond();

        //calculating number of days in between
        return dateAfterInt - dateBeforeInt;
    }

    public void clearMail(MailHandler mailHandler, User user, long uid) {
        MailModel mailModel = getMailModel(uid, user.getZone());
        Iterator<MailVO> iterator = mailModel.listMail.iterator();
        int size = mailModel.listMail.size();
        while (size > 0) {
            MailVO mailVO = iterator.next();
            if (mailVO.statusRead == EMailDefine.READ.getStatus() && mailVO.statusReceive == EMailDefine.READ.getStatus()) {
                iterator.remove();
            }
            size--;
        }
        if (mailModel.saveToDB(user.getZone())) {
            SendDeleteAllMail send = new SendDeleteAllMail();
            mailHandler.send(send, user);
            return;
        }
        SendDeleteAllMail send = new SendDeleteAllMail(ServerConstant.ErrorCode.ERR_SYS);
        mailHandler.send(send, user);
    }


    public boolean createMail(Zone zone, long uid, String title, String content, List<ResourcePackage> listGift) {
        MailModel mailModel = MailManager.getInstance().getMailModel(uid, zone);
        mailModel.listMail.add(new MailVO(title, content, listGift));
        return mailModel.saveToDB(zone);
    }

    public boolean createMailAdmin(Zone zone, String title, String content, List<ResourcePackage> listGift) {
        MailAdminModel mailAdminModel = getMailAdminModel(zone);
        mailAdminModel.listMail.add(new MailVO(title, content, listGift));
        return mailAdminModel.saveToDB(zone);
    }


    public void updateMail(Zone zone, MailAdminModel mailAdminModel, MailModel mailModel) {
        OUTERLOOP:
        for (MailVO admin : mailAdminModel.listMail) {
            for (String idMail : mailModel.listIdMail) {
                if (idMail.equals(admin.idMail)) {
                    continue OUTERLOOP;
                }
            }

            if (checkTimeExpire(admin.time) && checkTheFirstLogin(zone, admin.time, mailModel.uid)) {
                mailModel.listIdMail.add(admin.idMail);
                mailModel.listMail.add(admin.cloneMail());
            }
        }

        //Check mail user expire
        Iterator<MailVO> iterator = mailModel.listMail.iterator();
        while (iterator.hasNext()){
            if (!checkTimeExpire(iterator.next().time)){
                iterator.remove();
            }
        }

        mailModel.saveToDB(zone);
    }

    private boolean checkTheFirstLogin(Zone zone, String time, long uid) {
        UserModel um = ((ZoneExtension) zone.getExtension()).getUserManager().getUserModel(uid);
        if(um == null) return false;

        try {
            int timeUserFirstLogin = um.createTime;
            int timeMailFirstCreate = (int) (AdventureManager.getInstance().convertStringToDate(time).getTime() / 1000);
            return timeUserFirstLogin <= timeMailFirstCreate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkNewMail(Zone zone, long uid){
        MailModel mailModel = getMailModel(uid, zone);
        for (MailVO mailVO: mailModel.listMail){
            if (mailVO.statusReceive || mailVO.statusRead){
                return EMailDefine.UNREAD.getStatus();
            }
        }
        return EMailDefine.READ.getStatus();
    }
}
