package com.bamisu.log.sdk.module.admin;

import com.bamisu.log.sdk.module.gamethriftclient.IAP.entities.InfoIAPSale;
import com.bamisu.log.sdk.module.gamethriftclient.event.entities.EventConfigManager;
import com.bamisu.log.sdk.module.gamethriftclient.mail.entities.MailInfo;
import com.bamisu.log.sdk.module.gamethriftclient.event.entities.InfoEventNoti;
import com.bamisu.log.sdk.module.multiserver.MultiServerManager;
import com.bamisu.gamelib.httpserver.ServletBase;
import com.bamisu.gamelib.utils.ServletUtil;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.SmartFoxServer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 11:43 AM, 4/23/2020
 */
public class AdminHttp extends ServletBase {

    private SmartFoxServer sfs;

    @Override
    public void init() throws ServletException {
        sfs = SmartFoxServer.getInstance();
    }

    @Override
    protected void process(HttpServletRequest req, HttpServletResponse resp) {
        super.process(req, resp);
        fixHeaders(resp);
        String key = ServletUtil.getStringParameter(req, "key", "");
        if(!key.equals(Utils.loadFile(System.getProperty("user.dir") + "/conf/sdk/api-key"))){
            responseText("You are accessing unauthorized. We will save your IP address and send it to the network security department", resp);
            return;
        }

        String method = ServletUtil.getStringParameter(req, "cmd", "");
        switch (method) {
            case "start_maintenace":
                startMaintenace(req, resp);
                break;
            case "getCCU":
                getCCU(req, resp);
                break;
            case "send_mail":
                sendMailToPlayer(req, resp);
                break;
            case "saleIAP":
                saleIAP(req, resp);
                break;
            case "remove_saleIAP":
                removeSaleIAP(req, resp);
                break;
            case "update_refferal_code_user":
                updateRefferalCodeUser(req, resp);
                break;
            case "addEvent":
                addEvent(req, resp);
                break;
            case "removeEvent":
                removeEvent(req, resp);
                break;
            case "addEventSpecial":
                addEventSpecial(req, resp);
                break;
            case "removeEventSpecial":
                removeEventSpecial(req, resp);
                break;
            case "activeEventModule":
                activeEventModule(req, resp);
                break;
            case "black_friday":
                black_friday(req, resp);
                break;
            case "christmas":
                christmas(req, resp);
                break;
            case "new_year":
                new_year(req, resp);
                break;
            case "buyIAP":
                buy_IAP(req, resp);
                break;
            default:
                responseJson("404", resp);
        }
    }

    /**
     * Bat dau bao tri server
     * @param req
     * @param resp
     */
    private void startMaintenace(HttpServletRequest req, HttpServletResponse resp){
//        responseText(MultiServerManager.getInstance().maintenanceServer(), resp);
    }

    /**
     * Bat dau bao tri server
     * @param req
     * @param resp
     */
    private void getCCU(HttpServletRequest req, HttpServletResponse resp){
        responseJson(Utils.toJson(MultiServerManager.getInstance().getCCUAllServer()), resp);
    }

    /**
     * Gui mail den player
     * @param req
     * @param resp
     */
    private void sendMailToPlayer(HttpServletRequest req, HttpServletResponse resp){
        int serverID = ServletUtil.getIntParameter(req, "serverID", 0);
        List<Long> uids = ServletUtil.getListLongParameter(req, "uids", ",");
        String title = ServletUtil.getStringParameter(req, "title", "ADMIN");
        String content = ServletUtil.getStringParameter(req, "content", "");
        String gift = ServletUtil.getStringParameter(req, "gift", "[]");

        responseText(MultiServerManager.getInstance().sendMailToPlayer(serverID, uids, MailInfo.create(title, content, gift)), resp);
//                MailInfo.create(TextID.TITLE_MAIL_NOTIFY, TextID.CONTENT_MAIL_SERVER_MAINTENANCE, "[{\"id\": \"MON1000\", \"amount\": \"1000\"}]");
    }

    /**
     * Add sale cho IAP
     * @param req
     * @param resp
     */
    private void saleIAP(HttpServletRequest req, HttpServletResponse resp) {
        int serverID = ServletUtil.getIntParameter(req, "addr", 0);
        String id = ServletUtil.getStringParameter(req, "id", "");
        String idSale = ServletUtil.getStringParameter(req, "idSale", "");
        double cost = ServletUtil.getDoubleParameter(req, "cost", 0d);
        int time = ServletUtil.getIntParameter(req, "time", -1);
        List<Long> target = ServletUtil.getListLongParameter(req, "target", ",");
        if(target == null) target = new ArrayList<>();

        boolean result = false;
        if(id.isEmpty() || idSale.isEmpty()){
            result = false;
        }else {
            result = MultiServerManager.getInstance().addSaleIAP(serverID, InfoIAPSale.create(id, idSale, (float) cost, time, target));
        }

        responseText(result, resp);
    }

    /**
     * Remove sale IAP
     * @param req
     * @param resp
     */
    private void removeSaleIAP(HttpServletRequest req, HttpServletResponse resp) {
        int serverID = ServletUtil.getIntParameter(req, "serverID", 0);
        List<String> listIdSale = ServletUtil.getListStringParameter(req, "sale", ",");

        boolean result = MultiServerManager.getInstance().removeSaleIAP(serverID, listIdSale);

        responseText(result, resp);
    }

    /**
     * Thay doi ma moi nguoi choi
     * @param req
     * @param resp
     */
    private void updateRefferalCodeUser(HttpServletRequest req, HttpServletResponse resp){
        String codeOld = ServletUtil.getStringParameter(req, "old", "");
        String codeNew = ServletUtil.getStringParameter(req, "new", "");

        if(codeOld.isEmpty() || codeNew.isEmpty()){
            responseText("false", resp);
        }

        responseText(Utils.toJson(MultiServerManager.getInstance().updateCodeRefferalUser(codeOld, codeNew)), resp);
    }

    /**
     * Add event noti
     * @param req
     * @param resp
     */
    private void addEvent(HttpServletRequest req, HttpServletResponse resp) {
        int serverID = ServletUtil.getIntParameter(req, "serverID", 0);
        String id = ServletUtil.getStringParameter(req, "id", "");
        int time = ServletUtil.getIntParameter(req, "time", -1);
        List<Long> target = ServletUtil.getListLongParameter(req, "target", ",");
        if(target == null) target = new ArrayList<>();

        boolean result = false;
        if(id.isEmpty()){
            result = false;
        }else {
            result = MultiServerManager.getInstance().addEventGeneral(serverID, InfoEventNoti.create(id, time));
        }

        responseText(result, resp);
    }

    /**
     * Remove event notify
     * @param req
     * @param resp
     */
    private void removeEvent(HttpServletRequest req, HttpServletResponse resp) {
        int serverID = ServletUtil.getIntParameter(req, "serverID", 0);
        List<String> listIdEvent = ServletUtil.getListStringParameter(req, "id", ",");

        boolean result = MultiServerManager.getInstance().removeEventGeneral(serverID, listIdEvent);

        responseText(result, resp);
    }

    /**
     * Add event noti
     * @param req
     * @param resp
     */
    private void addEventSpecial(HttpServletRequest req, HttpServletResponse resp) {
        int serverID = ServletUtil.getIntParameter(req, "serverID", 0);
        String id = ServletUtil.getStringParameter(req, "id", "");
        int time = ServletUtil.getIntParameter(req, "time", -1);
        List<Long> target = ServletUtil.getListLongParameter(req, "target", ",");
        if(target == null) target = new ArrayList<>();

        boolean result = false;
        if(id.isEmpty()){
            result = false;
        }else {
            result = MultiServerManager.getInstance().addEventSpecial(serverID, InfoEventNoti.create(id, time));
        }

        responseText(result, resp);
    }

    /**
     * Remove event notify
     * @param req
     * @param resp
     */
    private void removeEventSpecial(HttpServletRequest req, HttpServletResponse resp) {
        int serverID = ServletUtil.getIntParameter(req, "serverID", 0);
        String zone = ServletUtil.getStringParameter(req, "zone", "");
        List<String> listIdEvent = ServletUtil.getListStringParameter(req, "id", ",");

        boolean result = MultiServerManager.getInstance().removeEventSpecial(serverID, listIdEvent);

        responseText(result, resp);
    }

    /**
     *
     * @param req
     * @param resp
     */
    private void activeEventModule(HttpServletRequest req, HttpServletResponse resp) {
        int serverID = ServletUtil.getIntParameter(req, "serverID", 0);
        String module = ServletUtil.getStringParameter(req, "module", "");
        boolean active = !ServletUtil.getStringParameter(req, "active", "false").equals("false");
        int timeStamp = ServletUtil.getIntParameter(req, "time", -1);

        boolean result = MultiServerManager.getInstance().updateEventModuleServer(serverID, module, active, timeStamp);

        responseText(result, resp);
    }


    private void black_friday(HttpServletRequest req, HttpServletResponse resp){
        int time = EventConfigManager.getInstance().getTimeEndEvent("black_friday");
        int now = Utils.getTimestampInSecond();
        if(time <= now){
            responseText(false, resp);
        }

        MultiServerManager.getInstance().addSaleIAP(0, InfoIAPSale.create("prestige1", "prestige1_sale", (float) 12.49, time - now, new ArrayList<>()));
        MultiServerManager.getInstance().addSaleIAP(0, InfoIAPSale.create("prestige2", "prestige2_sale", (float) 14.99, time - now, new ArrayList<>()));
        MultiServerManager.getInstance().addSaleIAP(0, InfoIAPSale.create("prestige3", "prestige3_sale", (float) 9.99, time - now, new ArrayList<>()));

        MultiServerManager.getInstance().addEventSpecial(0, InfoEventNoti.create("sale", time));
        MultiServerManager.getInstance().addEventSpecial(0, InfoEventNoti.create("black_friday", time));

        responseText(true, resp);
    }

    private void christmas(HttpServletRequest req, HttpServletResponse resp){
        int time = EventConfigManager.getInstance().getTimeEndEvent("christmas");
        int now = Utils.getTimestampInSecond();
        if(time <= now){
            responseText(false, resp);
        }

        MultiServerManager.getInstance().addSaleIAP(0, InfoIAPSale.create("vip1_1m", "vip1_1m_sale", (float) 3.99, time - now, new ArrayList<>()));
        MultiServerManager.getInstance().addSaleIAP(0, InfoIAPSale.create("vip2_1m", "vip2_1m_sale", (float) 7.49, time - now, new ArrayList<>()));

        MultiServerManager.getInstance().addEventGeneral(0, InfoEventNoti.create("christmas", time));

        MultiServerManager.getInstance().updateEventModuleServer(0, "iap", true, time);
        MultiServerManager.getInstance().updateEventModuleServer(0, "quest", true, time);


        responseText(true, resp);
    }

    private void new_year(HttpServletRequest req, HttpServletResponse resp){
        int time = EventConfigManager.getInstance().getTimeEndEvent("new_year");
        int now = Utils.getTimestampInSecond();
        if(time <= now){
            responseText(false, resp);
        }

        MultiServerManager.getInstance().addSaleIAP(0, InfoIAPSale.create("prestige1", "prestige1_sale", (float) 12.49, time - now, new ArrayList<>()));
        MultiServerManager.getInstance().addSaleIAP(0, InfoIAPSale.create("prestige2", "prestige2_sale", (float) 14.99, time - now, new ArrayList<>()));
        MultiServerManager.getInstance().addSaleIAP(0, InfoIAPSale.create("prestige3", "prestige3_sale", (float) 9.99, time - now, new ArrayList<>()));

        MultiServerManager.getInstance().addEventSpecial(0, InfoEventNoti.create("sale", time));
        MultiServerManager.getInstance().addEventSpecial(0, InfoEventNoti.create("new_year", time));

        responseText(true, resp);
    }

    private void buy_IAP(HttpServletRequest req, HttpServletResponse resp){
        int serverID = ServletUtil.getIntParameter(req, "serverID", 0);
        int uid = ServletUtil.getIntParameter(req, "uid", 0);
        String id = ServletUtil.getStringParameter(req, "id", "");

        boolean result = MultiServerManager.getInstance().buyPackageIAP(serverID, Long.valueOf(uid), id);

        responseText(result, resp);
    }
}
