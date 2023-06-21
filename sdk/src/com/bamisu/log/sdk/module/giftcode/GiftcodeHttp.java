package com.bamisu.log.sdk.module.giftcode;

import com.bamisu.gamelib.sql.sdk.dao.GiftcodeDAO;
import com.bamisu.gamelib.sql.sdk.dbo.GiftcodeDBO;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.log.sdk.module.giftcode.entities.GiftCodeExtra;
import com.bamisu.log.sdk.module.giftcode.model.GiftcodeModel;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.httpserver.ServletBase;
import com.bamisu.gamelib.utils.ServletUtil;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.sdk.module.sql.SDKsqlManager;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.data.SFSArray;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 10:12 AM, 4/22/2020
 */
public class GiftcodeHttp extends ServletBase {

    private SmartFoxServer sfs;

    @Override
    public void init() throws ServletException {
        sfs = SmartFoxServer.getInstance();
    }

    @Override
    protected void process(HttpServletRequest req, HttpServletResponse resp) {
        super.process(req, resp);
        fixHeaders(resp);
        String method = ServletUtil.getStringParameter(req, "cmd", "");
        String key = ServletUtil.getStringParameter(req, "key", "");
        if(!key.equals(Utils.loadFile(System.getProperty("user.dir") + "/conf/sdk/api-key"))){
            responseText("You are accessing unauthorized. We will save your IP address and send it to the network security department", resp);
            return;
        }

        switch (method) {
            case "create-giftcode":
                createGiftcode(req, resp);
                break;
            case "create-giftcode2":
                createGiftcode2(req, resp);
                break;
            case "check-giftcode":
                checkGiftcode(req, resp);
                break;
            case "active-giftcode":
                activeGiftcode(req, resp);
                break;
        }
    }

    private void activeGiftcode(HttpServletRequest req, HttpServletResponse resp) {
//        SFSObject respData = new SFSObject();
//
//        String code = req.getParameter(Params.CODE);
//        int serverID = Integer.parseInt(req.getParameter(Params.SERVER_ID));
//        long uid = Long.parseLong(req.getParameter(Params.UID));
//
//        GiftcodeModel giftcodeModel = GiftcodeModel.copyFromDB(code, SDKDatacontroler.getInstance());
//        ActiveGiftcodeResult activeGiftcodeResult = GiftcodeManager.getInstance().activeCode(
//                giftcodeModel,
//                serverID,
//                uid
//        );
//
//        if(activeGiftcodeResult == ActiveGiftcodeResult.SUCCESS){
//            respData.putInt(Params.SERVER_ID, serverID);
//            respData.putLong(Params.UID, uid);
//            respData.putSFSArray(Params.GIFTS, SFSArray.newFromJsonData(Utils.toJson(giftcodeModel.gifts)));
//        }else {
//            respData.putUtfString(Params.ERROR, activeGiftcodeResult.name());
//        }
    }

    private void createGiftcode(HttpServletRequest req, HttpServletResponse resp) {
        String x = req.getParameter("expired");
        int expired = Integer.parseInt(x);
        int num = Integer.parseInt(req.getParameter("num"));
        List<ResourcePackage> gifts = new ArrayList<>();
        SFSArray sfsArray = SFSArray.newFromJsonData(req.getParameter("gifts"));
        List<GiftCodeExtra> extras = new ArrayList<>();
        SFSArray sfsArrayExtra = SFSArray.newFromJsonData(req.getParameter("extra"));
        for (int i = 0; i < sfsArray.size(); i++){
            gifts.add(Utils.fromJson(sfsArray.getSFSObject(i).toJson(), ResourcePackage.class));
        }

        for (int i = 0; i < sfsArrayExtra.size(); i++){
            extras.add(Utils.fromJson(sfsArrayExtra.getSFSObject(i).toJson(), GiftCodeExtra.class));
        }

        List<GiftcodeModel> giftcodeModels = GiftcodeManager.getInstance().genCode(num, expired, gifts, extras);
        List<String> codes = new ArrayList<>();
        List<GiftcodeDBO> lst = new ArrayList<>();
        for (GiftcodeModel giftcodeModel : giftcodeModels) {
            lst.add(new GiftcodeDBO(giftcodeModel.code, giftcodeModel.expired));
            codes.add(giftcodeModel.code);
        }

        if (!lst.isEmpty()) {
            GiftcodeDAO.create(lst, SDKsqlManager.getInstance().getSqlController());
        }

        responseJson(Utils.toJson(codes), resp);
    }

    private void createGiftcode2(HttpServletRequest req, HttpServletResponse resp) {
        String x = req.getParameter("expired");
        int expired = Integer.parseInt(x);
        int max = Integer.parseInt(req.getParameter("max"));
        String code = req.getParameter("code");
        List<ResourcePackage> gifts = new ArrayList<>();
        SFSArray sfsArray = SFSArray.newFromJsonData(req.getParameter("gifts"));
        List<GiftCodeExtra> extras = new ArrayList<>();
        SFSArray sfsArrayExtra = SFSArray.newFromJsonData(req.getParameter("extra"));
        for (int i = 0; i < sfsArray.size(); i++){
            gifts.add(Utils.fromJson(sfsArray.getSFSObject(i).toJson(), ResourcePackage.class));
        }

        for (int i = 0; i < sfsArrayExtra.size(); i++){
            extras.add(Utils.fromJson(sfsArrayExtra.getSFSObject(i).toJson(), GiftCodeExtra.class));
        }

        List<GiftcodeModel> giftcodeModels = GiftcodeManager.getInstance().genCode(code, expired, max, gifts, extras);
        List<String> codes = new ArrayList<>();
        List<GiftcodeDBO> lst = new ArrayList<>();
        for (GiftcodeModel giftcodeModel : giftcodeModels) {
            lst.add(new GiftcodeDBO(giftcodeModel.code, giftcodeModel.expired));
            codes.add(giftcodeModel.code);
        }

        if (!lst.isEmpty()) {
            GiftcodeDAO.create(lst, SDKsqlManager.getInstance().getSqlController());
        }

        responseJson(Utils.toJson(codes), resp);
    }

    private void checkGiftcode(HttpServletRequest req, HttpServletResponse resp) {
        String code = req.getParameter("code").toLowerCase();
        responseJson(GiftcodeModel.copyFromDB(code, SDKDatacontroler.getInstance()).toJson(), resp);
    }
}
