package com.bamisu.log.gameserver.module.mail;

import com.bamisu.log.gameserver.datamodel.mail.MailModel;
import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.language.LanguageUtils;
import com.bamisu.gamelib.language.TextID;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.gamelib.utils.extension.ExtensionUtility;
import com.bamisu.log.gameserver.module.notification.NotificationManager;
import com.bamisu.log.gameserver.module.notification.defind.ENotification;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MailUtils {
    private static MailUtils instance;

    public static MailUtils getInstance(){
        if (instance == null){
            instance = new MailUtils();
        }
        return instance;
    }

    private MailUtils(){
    }

    //-----------TEST----------
    public void sendTestMail(MailHandler handler, long uid, User user, List<ResourcePackage> listGift, int ozil, int leno){
        MailManager.getInstance().createMail(user.getZone(), uid , LanguageUtils.toTemplate(TextID.TITLE_MAIL_TEST), LanguageUtils.toTemplate(TextID.CONTENT_MAIL_TEST, Arrays.asList(ozil, leno)), listGift);
        NotificationManager.getInstance().sendNotify(uid, Collections.singletonList(ENotification.HAVE_MAIL_NOT_SEE_OR_COLLECT.getNotifyID(null)), handler.getParentExtension().getParentZone());
    }

    public void sendMailFragment(MailHandler handler, long uid, User user, List<ResourcePackage> listGift, List<String> listContent) {
        MailManager.getInstance().createMail(user.getZone(), uid , LanguageUtils.toTemplate(TextID.TITLE_MAIL_FRAGMENT_HERO), LanguageUtils.toTemplate(TextID.CONTENT_MAIL_FRAGMENT_HERO, Collections.singletonList(listContent)), listGift);
        NotificationManager.getInstance().sendNotify(uid, Collections.singletonList(ENotification.HAVE_MAIL_NOT_SEE_OR_COLLECT.getNotifyID(null)), handler.getParentExtension().getParentZone());
    }

    public void sendMailDiamondGiftVIP(MailHandler handler, long uid, User user, List<ResourcePackage> listGift, String title, String content){
        MailManager.getInstance().createMail(user.getZone(), uid , LanguageUtils.toTemplate(title), LanguageUtils.toTemplate(content), listGift);
        NotificationManager.getInstance().sendNotify(uid, Collections.singletonList(ENotification.HAVE_MAIL_NOT_SEE_OR_COLLECT.getNotifyID(null)), handler.getParentExtension().getParentZone());
    }

    public void sendMailFixBugAFK(long uid, List<ResourcePackage> listGift, Zone zone){
        MailManager.getInstance().createMail(zone, uid,
                LanguageUtils.toTemplate("0", Arrays.asList("Bug Fix Account (Big)")),
                LanguageUtils.toTemplate("0", Arrays.asList("Dear Player, \\n We have detected a critical bug occurring to your account. We have adjusted your account to status prior to when this bug occurred. We apologize for this incovenience. In compensation, please enjoy a large gift of 5000 diamonds. \\n Thank you for your support of our game. \\n Sincerely, -Server Admin")),
                listGift);
        NotificationManager.getInstance().sendNotify(uid, Collections.singletonList(ENotification.HAVE_MAIL_NOT_SEE_OR_COLLECT.getNotifyID(null)), zone);
    }

    public void sendMailUser(long uid, List<ResourcePackage> listGift, String title, String content, List<Object> params, Zone zone){
        MailManager.getInstance().createMail(zone, uid , LanguageUtils.toTemplate(title), LanguageUtils.toTemplate(content, params), listGift);
        NotificationManager.getInstance().sendNotify(uid, Collections.singletonList(ENotification.HAVE_MAIL_NOT_SEE_OR_COLLECT.getNotifyID(null)), zone);
    }

    public void sendMailDarkRealm_Solo(long uid, List<ResourcePackage> listGift, Zone zone){
        MailManager.getInstance().createMail(zone, uid,
                LanguageUtils.toTemplate(TextID.TITLE_MAIL_DarkRealm_Solo),
                LanguageUtils.toTemplate(TextID.CONTENT_MAIL_DarkRealm_Solo),
                listGift);
        NotificationManager.getInstance().sendNotify(uid, Collections.singletonList(ENotification.HAVE_MAIL_NOT_SEE_OR_COLLECT.getNotifyID(null)), zone);
    }

    public void sendMailDarkRealm_Alliance(long uid, List<ResourcePackage> listGift, Zone zone){
        MailManager.getInstance().createMail(zone, uid,
                LanguageUtils.toTemplate(TextID.TITLE_MAIL_DarkRealm_Alliance),
                LanguageUtils.toTemplate(TextID.CONTENT_MAIL_DarkRealm_Alliance),
                listGift);

        NotificationManager.getInstance().sendNotify(uid, Collections.singletonList(ENotification.HAVE_MAIL_NOT_SEE_OR_COLLECT.getNotifyID(null)), zone);
    }

    public void sendMailEndless_Solo(long uid, List<ResourcePackage> listGift, Zone zone){
        MailManager.getInstance().createMail(zone, uid,
                LanguageUtils.toTemplate(TextID.TITLE_MAIL_Endless_Solo),
                LanguageUtils.toTemplate(TextID.CONTENT_MAIL_Endless_Solo),
                listGift);
        NotificationManager.getInstance().sendNotify(uid, Collections.singletonList(ENotification.HAVE_MAIL_NOT_SEE_OR_COLLECT.getNotifyID(null)), zone);
    }

    public void sendMailEndless_Alliance(long uid, List<ResourcePackage> listGift, Zone zone){
        MailManager.getInstance().createMail(zone, uid,
                LanguageUtils.toTemplate(TextID.TITLE_MAIL_Endless_Alliance),
                LanguageUtils.toTemplate(TextID.CONTENT_MAIL_Endless_Alliance),
                listGift);
        NotificationManager.getInstance().sendNotify(uid, Collections.singletonList(ENotification.HAVE_MAIL_NOT_SEE_OR_COLLECT.getNotifyID(null)), zone);
    }

    public String sendMailToPlayer(Zone zone, List<Long> uids, List<ResourcePackage> listGift, String title, String content){
        SFSObject data = new SFSObject();
        data.putLongArray(Params.UIDS, uids);
        data.putUtfString(Params.TITLE, title);
        data.putUtfString(Params.CONTENT, content);
        data.putUtfString(Params.GIFT_LIST, Utils.toJson(listGift));

        ISFSObject objGet = (ISFSObject) zone.getExtension().handleInternalMessage(CMD.InternalMessage.SEND_MAIL_TO_PLAYER, data);
        if(!objGet.containsKey(Params.DATA)) return "";

        return objGet.getUtfString(Params.DATA);
    }
}

