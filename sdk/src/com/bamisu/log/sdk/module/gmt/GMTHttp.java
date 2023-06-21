package com.bamisu.log.sdk.module.gmt;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.entities.ServerInfo;
import com.bamisu.gamelib.httpserver.ServletBase;
import com.bamisu.gamelib.sql.sdk.dao.AccountDAO;
import com.bamisu.gamelib.sql.sdk.dao.LinkDAO;
import com.bamisu.log.sdk.module.multiserver.MultiServerManager;
import com.bamisu.log.sdk.module.sdkthriftserver.ThriftUtils;
import com.bamisu.log.sdk.module.sql.SDKsqlManager;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

public class GMTHttp extends ServletBase {
    @Override
    public void init() throws ServletException {
    }

    @Override
    protected void process(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("process" + req.getQueryString());
        super.process(req, resp);
        fixHeaders(resp);
        System.out.println("process" + req.getQueryString());

//        String key = ServletUtil.getStringParameter(req, "token", "");
//        if(!key.equals("IiDBoNLLx6S7RX0EVw9ELNvn9y00cr5LxzaLcMkAG3vlOmPzGP3gqntnLhbMw7lTwluwVDWpwgAaqgAWTr09EilepmQMjZtDaO0UhK9BQAz6EXRnfFkJNvN1GrBNgUhf")){
//            responseText("You are accessing unauthorized. We will save your IP address and send it to the network security department", resp);
//        }

        RecPackage recPackage = new RecPackage(req);
        String method = recPackage.data.getUtfString("cmd");
        switch (method) {
            case "count_linked_account_all_time":
                countLinkedAllTime(req, recPackage.data, resp);
                break;
            case "count_linked_account_on_month":
                countLinkedOnMonth(req, recPackage.data, resp);
                break;
            case "count_linked_account_on_year":
                countLinkedOnYear(req, recPackage.data, resp);
                break;
            case "count_account_by_referralcode":
                countAccountByRefrralCode(req, recPackage.data, resp);
                break;
            case "count_account_linked_by_referralcode":
                countAccountLinkedByRefrralCode(req, recPackage.data, resp);
                break;
            case "count_account_50_by_referralcode":
                countAccount50ByRefrralCode(req, recPackage.data, resp);
                break;
            default:
                responseJson(new SendPackage(GMTErrorCode.CMD_NOT_FOUND).toJson(), resp);
        }
    }

    private void countAccountByRefrralCode(HttpServletRequest req, ISFSObject data, HttpServletResponse resp) {
        RecPackage recPackage = new RecPackage(req);
        String refcode = recPackage.data.getUtfString("refcode");

        SendPackage sendPackage = new SendPackage();
        sendPackage.data.putInt(Params.COUNT, AccountDAO.countAccountByRefrralCode(
                SDKsqlManager.getInstance().getSqlController(), refcode));
        responseJson(sendPackage.toJson(), resp);
    }

    private void countAccountLinkedByRefrralCode(HttpServletRequest req, ISFSObject data, HttpServletResponse resp) {
        RecPackage recPackage = new RecPackage(req);
        String refcode = recPackage.data.getUtfString("refcode");

        SendPackage sendPackage = new SendPackage();
        sendPackage.data.putInt(Params.COUNT, AccountDAO.countAccountLinkedByRefrralCode(
                SDKsqlManager.getInstance().getSqlController(), refcode));
        responseJson(sendPackage.toJson(), resp);
    }

    private void countAccount50ByRefrralCode(HttpServletRequest req, ISFSObject data, HttpServletResponse resp) {
        String refcode = data.getUtfString("refcode");

        SendPackage sendPackage = new SendPackage();
        sendPackage.data.putInt(Params.COUNT, AccountDAO.countAccount50ByRefrralCode(
                SDKsqlManager.getInstance().getSqlController(), refcode));
        responseJson(sendPackage.toJson(), resp);
    }

    private void countLinkedAllTime(HttpServletRequest req, ISFSObject data, HttpServletResponse resp) {
        SendPackage sendPackage = new SendPackage();
        sendPackage.data.putInt(Params.COUNT, LinkDAO.countLinkedAllTime(
                SDKsqlManager.getInstance().getSqlController()));
        responseJson(sendPackage.toJson(), resp);
    }

    private void countLinkedOnMonth(HttpServletRequest req, ISFSObject data, HttpServletResponse resp) {
        String month = data.getUtfString("month");

        SendPackage sendPackage = new SendPackage();
        sendPackage.data.putSFSArray(Params.LIST_DAY, LinkDAO.countLinkedOnMonth(
                SDKsqlManager.getInstance().getSqlController(), month));
        responseJson(sendPackage.toJson(), resp);
    }

    private void countLinkedOnYear(HttpServletRequest req, ISFSObject data, HttpServletResponse resp) {
        String year = data.getUtfString("year");

        SendPackage sendPackage = new SendPackage();
        sendPackage.data.putSFSArray(Params.LIST_MONTH, LinkDAO.countLinkedOnYear(
                SDKsqlManager.getInstance().getSqlController(), year));
        responseJson(sendPackage.toJson(), resp);
    }
}
