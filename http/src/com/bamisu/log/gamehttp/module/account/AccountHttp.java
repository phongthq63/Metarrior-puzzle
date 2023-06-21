package com.bamisu.log.gamehttp.module.account;

import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.httpserver.ServletBase;
import com.bamisu.gamelib.utils.ServletUtil;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Quach Thanh Phong
 * On 2/26/2022 - 10:53 PM
 */
public class AccountHttp extends ServletBase {

    @WithSpan
    @Override
    protected void process(HttpServletRequest req, HttpServletResponse resp) {
        super.process(req, resp);
        fixHeaders(resp);
        try {
            String method = ServletUtil.getStringParameter(req, "cmd", "");
            switch (method) {
                case "update_username":
                    this.updateUsername(req, resp);
                    break;
                case "update_password":
                    this.changePassword(req, resp);
                    break;
                case "get_profile":
                    this.getProfile(req, resp);
                    break;
                case "login":
                    handlerLogin(req, resp);
                    break;
                case CMD.InternalMessage.DEPOSIT_TOKEN:
                    this.depositToken(req, resp);
                    break;

                case CMD.InternalMessage.WITHDRAW_TOKEN:
                    this.withdrawToken(req, resp);
                    break;

                case CMD.InternalMessage.REJECT_WITHDRAW_TOKEN:
                    this.rejectWithdrawToken(req, resp);
                    break;

                case CMD.InternalMessage.REQUEST_WITHDRAW_TOKEN:
                    this.requestWithdrawToken(req, resp);
                    break;

                case CMD.InternalMessage.HTTP_LINK_WALLET:
                    this.linkWallet(req, resp);
                    break;

                case CMD.HttpCMD.RESET_RMQ:
                    this.resetRmqConnection(req, resp);
                    break;

                default:
                    throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();

            Map<String,String> mapResponse = new HashMap<>();
            mapResponse.put(Params.ERROR_CODE, String.valueOf(ServerConstant.ErrorCode.ERR_BAD_EXTENSION));
            mapResponse.put(Params.MESS, "Invalid request!!!");
            responseJson(Utils.toJson(mapResponse), resp);
        }
    }

    private void resetRmqConnection(HttpServletRequest req, HttpServletResponse resp) {
        ISFSObject objPut = new SFSObject();
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        ISFSObject objGet = (ISFSObject) SmartFoxServer.getInstance()
                .getZoneManager().getZoneByName(server)
                .getExtension().handleInternalMessage(CMD.HttpCMD.RESET_RMQ, objPut);
        responseJson(objGet.toJson(), resp);
    }

    @WithSpan
    private void handlerLogin(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        String token = ServletUtil.getStringParameter(req, Params.TOKEN);
        int type = 1;
        String ip = req.getRemoteAddr();

        ISFSObject objPut = new SFSObject();
        objPut.putUtfString(Params.TOKEN, token);
        objPut.putInt(Params.TYPE, type);
        objPut.putUtfString(Params.IP, ip);

        ISFSObject objGet = (ISFSObject) SmartFoxServer.getInstance()
                .getZoneManager().getZoneByName(server)
                .getExtension().handleInternalMessage(CMD.InternalMessage.GET_LOGIN_INFO, objPut);
        responseJson(objGet.toJson(), resp);
    }


    /**
     * update username and password if account is not exists
     * @param req
     * @param resp
     */
    @WithSpan
    private void updateUsername(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        String username = ServletUtil.getStringParameter(req, Params.USER_NAME);
        String password = ServletUtil.getStringParameter(req, Params.USER_PASSWORD);
        String email = ServletUtil.getStringParameter(req, Params.USER_EMAIL).toLowerCase();
        String code = ServletUtil.getStringParameter(req, Params.CODE);
        long userId = ServletUtil.getLongParameter(req, Params.UID);
        ISFSObject res = new SFSObject();
        Zone zone = SmartFoxServer.getInstance()
                .getZoneManager().getZoneByName(server);
        if (zone == null) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
        } else {
            ISFSObject params = new SFSObject();
            params.putLong(Params.UID, userId);
            params.putText(Params.USER_NAME, username);
            params.putText(Params.USER_PASSWORD, password);
            params.putText(Params.USER_EMAIL, email);
            params.putText(Params.CODE, code);
            res = (ISFSObject) SmartFoxServer.getInstance()
                    .getZoneManager().getZoneByName(server)
                    .getExtension().handleInternalMessage(CMD.InternalMessage.UPDATE_USERNAME_PASSWORD, params);
        }

        responseJson(res.toJson(), resp);
    }

    @WithSpan
    private void changePassword(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        String password = ServletUtil.getStringParameter(req, Params.USER_PASSWORD);
        long userId = ServletUtil.getLongParameter(req, Params.UID);
        ISFSObject res = new SFSObject();
        Zone zone = SmartFoxServer.getInstance()
                .getZoneManager().getZoneByName(server);
        if (zone == null) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
        } else {
            ISFSObject params = new SFSObject();
            params.putLong(Params.UID, userId);
            params.putText(Params.USER_PASSWORD, password);
            res = (ISFSObject) SmartFoxServer.getInstance()
                    .getZoneManager().getZoneByName(server)
                    .getExtension().handleInternalMessage(CMD.InternalMessage.CHANGE_PASSWORD, params);
        }

        responseJson(res.toJson(), resp);
    }

    @WithSpan
    private void getProfile(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long userId = ServletUtil.getLongParameter(req, Params.UID);
        Map<String,String> mapResponse = new HashMap<>();
        Zone zone = SmartFoxServer.getInstance()
                .getZoneManager().getZoneByName(server);
        ISFSObject res = new SFSObject();
        if (zone == null) {
            res.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_SYS);
        } else {
            ISFSObject params = new SFSObject();
            params.putLong(Params.UID, userId);
            res = (ISFSObject) SmartFoxServer.getInstance()
                    .getZoneManager().getZoneByName(server)
                    .getExtension().handleInternalMessage(CMD.InternalMessage.GET_SOG_OF_USER, params);
        }

        responseJson(res.toJson(), resp);
    }

    @WithSpan
    private void depositToken(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long userId = ServletUtil.getLongParameter(req, Params.UID);
        String txhash = ServletUtil.getStringParameter(req, Params.TRANSACTION_ID);
        String name = ServletUtil.getStringParameter(req, Params.NAME);
        int count = ServletUtil.getIntParameter(req, Params.COUNT);
        ISFSObject data = new SFSObject();
        data.putText(Params.TRANSACTION_ID, txhash);
        data.putText(Params.NAME, name);
        data.putInt(Params.COUNT, count);
        data.putLong(Params.UID, userId);

        ISFSObject res = this.sendCmd(server, CMD.InternalMessage.DEPOSIT_TOKEN, data);
        responseJson(res.toJson(), resp);
    }

    @WithSpan
    private void withdrawToken(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long userId = ServletUtil.getLongParameter(req, Params.UID);
        String txhash = ServletUtil.getStringParameter(req, Params.TRANSACTION_ID, "");
        String tid = ServletUtil.getStringParameter(req, Params.ID, "");
        int quantity = ServletUtil.getIntParameter(req, Params.COUNT, 0);

        ISFSObject data = new SFSObject();
        data.putText(Params.ID, tid);
        data.putLong(Params.UID, userId);
        data.putText(Params.TRANSACTION_ID, txhash);
        data.putInt(Params.COUNT, quantity);
        ISFSObject res = this.sendCmd(server, CMD.InternalMessage.WITHDRAW_TOKEN, data);
        responseJson(res.toJson(), resp);
    }

    @WithSpan
    private void rejectWithdrawToken(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long userId = ServletUtil.getLongParameter(req, Params.UID);
        String tid = ServletUtil.getStringParameter(req, Params.ID, "");

        ISFSObject data = new SFSObject();
        data.putText(Params.ID, tid);
        data.putLong(Params.UID, userId);
        ISFSObject res = this.sendCmd(server, CMD.InternalMessage.REJECT_WITHDRAW_TOKEN, data);
        responseJson(res.toJson(), resp);
    }

    @WithSpan
    private void requestWithdrawToken(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long userId = ServletUtil.getLongParameter(req, Params.UID);
        String name = ServletUtil.getStringParameter(req, Params.NAME, "");
        double quantity = ServletUtil.getDoubleParameter(req, Params.COUNT, 0.0);
        String address = ServletUtil.getStringParameter(req, Params.ADDRESS, "");

        ISFSObject params = new SFSObject();
        params.putLong(Params.UID, userId);
        params.putText(Params.NAME, name);
        params.putDouble(Params.COUNT, quantity);
        ISFSObject res = this.sendCmd(server, CMD.InternalMessage.REQUEST_WITHDRAW_TOKEN, params);
        responseJson(res.toJson(), resp);
    }

    @WithSpan
    private void linkWallet(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long userId = ServletUtil.getLongParameter(req, Params.UID);
        String wallet = ServletUtil.getStringParameter(req, Params.WALLET, "");
        String username = ServletUtil.getStringParameter(req, Params.USER_NAME, "");
        String password = ServletUtil.getStringParameter(req, Params.USER_PASSWORD, "");
        ISFSObject params = new SFSObject();
        params.putLong(Params.UID, userId);
        params.putText(Params.WALLET, wallet);
        params.putText(Params.USER_PASSWORD, password);
        params.putText(Params.USER_NAME, username);
        ISFSObject res = this.sendCmd(server, CMD.InternalMessage.HTTP_LINK_WALLET, params);
        responseJson(res.toJson(), resp);
    }
}
