package com.bamisu.log.gamehttp.module.hero;

import com.bamisu.gamelib.entities.CMD;
import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.httpserver.ServletBase;
import com.bamisu.gamelib.utils.ServletUtil;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Quach Thanh Phong
 * On 2/27/2022 - 12:09 AM
 */
public class HeroHttp extends ServletBase {

    @WithSpan
    @Override
    protected void process(HttpServletRequest req, HttpServletResponse resp) {
        super.process(req, resp);
        fixHeaders(resp);
        try {
            String method = ServletUtil.getStringParameter(req, "cmd", "");
            switch (method) {
                case CMD.HttpCMD.GET_LIST_NFT_HERO_INFO:
                    handlerGetListNFTHeroInfo(req, resp);
                    break;
                case CMD.HttpCMD.MINT_NFT_HERO:
                    handlerMintNFTHero(req, resp);
                    break;
                case CMD.HttpCMD.VERIFY_NFT_HERO:
                    handlerVerifyNFTHero(req, resp);
                    break;
                case CMD.HttpCMD.SET_STATUS_NFT_HERO:
                    handlerSetStatusNFTHero(req, resp);
                    break;
                case CMD.HttpCMD.VERIFY_TRANFER_NFT_HERO:
                    handlerVerifyTranferNFTHero(req, resp);
                    break;
                case CMD.InternalMessage.HTTP_SUM_HERO:
                    this.handleSumHero(req, resp);
                    break;
                case CMD.InternalMessage.HTTP_CLAIM_HERO:

                    break;
                case CMD.HttpCMD.ASCEND_HERO:
                    this.handleAscend(req, resp);
                    break;
                case CMD.HttpCMD.CANCEL_ASCEND_HERO:
                    this.handleCancelAscend(req, resp);
                    break;
                case CMD.HttpCMD.CONFIRM_ASCEND_HERO:
                    this.handleConfirmAscend(req, resp);
                    break;
                case CMD.HttpCMD.GET_HERO_BREEDING:
                    this.getListHeroBreeding(req, resp);
                    break;
                case CMD.HttpCMD.CLAIM_HERO_BREEDING:
                    this.handleVerifyNftHeroBreed(req, resp);
                    break;
                case CMD.HttpCMD.LIST_HERO_ASCEND:
                    this.getListHeroAscend(req, resp);
                    break;
                case CMD.HttpCMD.GET_HERO_ASCEND_STATS:
                    this.getHeroAscendStats(req, resp);
                    break;
                case CMD.HttpCMD.CHECK_HERO_FOR_SALE:
                    this.checkHeroForSale(req, resp);
                    break;
                case CMD.HttpCMD.LIST_HERO_OPEN_BOX:
                    this.listHeroOpenBox(req, resp);
                    break;
                case "breed":
                    this.handleBred(req, resp);
                    break;
                case "countdown":
                    this.handleCountdown(req, resp);
                    break;
                case "list_hero":
                    this.getListHeroBreed(req, resp);
                    break;
                case "delete_hero":
                    this.handlerDeleteHero(req, resp);
                    break;
                case "delete_hero_block":
                    this.handlerDeleteHeroBlock(req, resp);
                    break;
                case "move_hero":
                    this.handleMoveHero(req, resp);
                    break;
                case "move_hero_upstar":
                    this.handleMoveHeroUpstar(req, resp);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String,String> mapResponse = new HashMap<>();
            mapResponse.put(Params.ERROR_CODE, String.valueOf(ServerConstant.ErrorCode.ERR_BAD_EXTENSION));
            mapResponse.put(Params.MESS, "Invalid request!!!");
            responseJson(Utils.toJson(mapResponse), resp);
        }
    }

    @WithSpan
    private void checkHeroForSale(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID, "2");
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        String hash = ServletUtil.getStringParameter(req, Params.HASH);
        ISFSObject params = new SFSObject();
        params.putLong("uid", uid);
        params.putText(Params.HASH, hash);
        ISFSObject res = this.sendCmd(server, CMD.HttpCMD.CHECK_HERO_FOR_SALE, params);
        responseJson(res.toJson(), resp);
    }

    @WithSpan
    private void listHeroOpenBox(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID, "2");
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        ISFSObject params = new SFSObject();
        params.putLong(Params.UID, uid);
        ISFSObject res = this.sendCmd(server, CMD.HttpCMD.LIST_HERO_OPEN_BOX, params);
        responseJson(res.toJson(), resp);
    }

    @WithSpan
    private void getListHeroBreed(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID, "2");
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        ISFSObject params = new SFSObject();
        params.putLong("uid", uid);
        ISFSObject res = this.sendCmd(server, "list_hero", params);
        responseJson(res.toJson(), resp);
    }

    @WithSpan
    private void handleCountdown(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID, "2");
        long uid = ServletUtil.getLongParameter(req, Params.UID);

        ISFSObject params = new SFSObject();
        params.putLong("uid", uid);
        ISFSObject objGet = (ISFSObject) SmartFoxServer.getInstance()
                .getZoneManager().getZoneByName(server)
                .getExtension().handleInternalMessage("countdown", params);
        responseJson(objGet.toJson(), resp);
    }

    @WithSpan
    private void handleBred(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID, "2");
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        String hash1 = ServletUtil.getStringParameter(req, "hash1");
        String hash2 = ServletUtil.getStringParameter(req, "hash2");
        int count = ServletUtil.getIntParameter(req, "count");
        int bannerId = 0;
        String resource = "MIXED";

        ISFSObject params = new SFSObject();
        params.putText("hash1", hash1);
        params.putText("hash2", hash2);
        params.putText("rType", resource);
        params.putInt("id", bannerId);
        params.putInt("count", count);
        params.putLong("uid", uid);
        ISFSObject objGet = (ISFSObject) SmartFoxServer.getInstance()
                .getZoneManager().getZoneByName(server)
                .getExtension().handleInternalMessage("breed", params);
        responseJson(objGet.toJson(), resp);
    }

    /**
     *
     * @param req
     * @param resp
     */
    @WithSpan
    private void handlerGetListNFTHeroInfo(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long uid = ServletUtil.getLongParameter(req, Params.UID);

        ISFSObject objPut = new SFSObject();
        objPut.putLong(Params.UID, uid);

        ISFSObject objGet = (ISFSObject) SmartFoxServer.getInstance()
                .getZoneManager().getZoneByName(server)
                .getExtension().handleInternalMessage(CMD.InternalMessage.GET_NFT_HERO_LIST, objPut);
        responseJson(objGet.toJson(), resp);
    }

    /**
     *
     * @param req
     * @param resp
     */
    @WithSpan
    private void handlerMintNFTHero(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        String data = ServletUtil.getBody(req);

        if(data.isEmpty()) {
            responseJson("{\"mess\":\"Invalid data\",\"ec\":5002}", resp);
            return;
        }

        ISFSObject objPut = new SFSObject();
        objPut.putLong(Params.UID, uid);
        objPut.putUtfString(Params.DATA, data);

        ISFSObject objGet = (ISFSObject) SmartFoxServer.getInstance()
                .getZoneManager().getZoneByName(server)
                .getExtension().handleInternalMessage(CMD.InternalMessage.MINT_NFT_HERO, objPut);
        responseJson(objGet.toJson(), resp);
    }

    /**
     *
     * @param req
     * @param resp
     */
    @WithSpan
    private void handlerVerifyNFTHero(HttpServletRequest req, HttpServletResponse resp) {
        ISFSObject params = new SFSObject();
        params.putBool("isMintBox", true);
        this.verifyNFTHero(req, resp, params);
    }

    @WithSpan
    private void handleVerifyNftHeroBreed(HttpServletRequest req, HttpServletResponse resp) {
        ISFSObject params = new SFSObject();
        params.putBool("isMintBox", false);
        this.verifyNFTHero(req, resp, params);
    }

    @WithSpan
    private void verifyNFTHero(HttpServletRequest req, HttpServletResponse resp, ISFSObject objPut) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        String transactionHash = ServletUtil.getStringParameter(req, Params.TRANSACTION_ID);
        List<String> tokenId = ServletUtil.getListStringParameter(req, Params.TOKEN, ",");
        objPut.putLong(Params.UID, uid);
        objPut.putUtfString(Params.TRANSACTION_ID, transactionHash);
        objPut.putUtfStringArray(Params.TOKEN, tokenId);

        ISFSObject res = this.sendCmd(server, CMD.InternalMessage.VERIFY_MINT_NFT_HERO, objPut);
        responseJson(res.toJson(), resp);
    }

    /**
     *
     * @param req
     * @param resp
     */
    @WithSpan
    private void handlerSetStatusNFTHero(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        String hash = ServletUtil.getStringParameter(req, Params.HASH);
        int status = ServletUtil.getIntParameter(req, Params.STATUS);

        ISFSObject objPut = new SFSObject();
        objPut.putLong(Params.UID, uid);
        objPut.putUtfString(Params.HASH, hash);

        ISFSObject objGet = new SFSObject();
        switch (status) {
            case 0:
                objGet = (ISFSObject) SmartFoxServer.getInstance()
                        .getZoneManager().getZoneByName(server)
                        .getExtension().handleInternalMessage(CMD.InternalMessage.UNLOCK_HERO, objPut);
                break;
            case 1:
                objGet = (ISFSObject) SmartFoxServer.getInstance()
                        .getZoneManager().getZoneByName(server)
                        .getExtension().handleInternalMessage(CMD.InternalMessage.LOCK_HERO, objPut);
                break;
            default:
                objGet.putShort(Params.ERROR_CODE, ServerConstant.ErrorCode.ERR_INVALID_VALUE);
                break;
        }
        responseJson(objGet.toJson(), resp);
    }

    /**
     *
     * @param req
     * @param resp
     */
    @WithSpan
    private void handlerVerifyTranferNFTHero(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        String transactionHash = ServletUtil.getStringParameter(req, Params.TRANSACTION_ID);

        ISFSObject objPut = new SFSObject();
        objPut.putUtfString(Params.TRANSACTION_ID, transactionHash);

        ISFSObject objGet = (ISFSObject) SmartFoxServer.getInstance()
                .getZoneManager().getZoneByName(server)
                .getExtension().handleInternalMessage(CMD.InternalMessage.TRANFER_NFT_HERO, objPut);
        responseJson(objGet.toJson(), resp);
    }

    /**
     * api sum hero for market
     * @param req
     * @param resp
     */
    @WithSpan
    private void handleSumHero(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        String id = ServletUtil.getStringParameter(req, Params.ID, "0");
        int count = ServletUtil.getIntParameter(req, Params.COUNT, 1);
        ISFSObject params = new SFSObject();
        params.putText(Params.ID, id);
        params.putShort(Params.COUNT, (short) count);
        params.putLong(Params.UID, uid);

        ISFSObject rec = this.sendCmd(server, CMD.InternalMessage.HTTP_SUM_HERO, params);
        responseJson(rec.toJson(), resp);
    }

    @WithSpan
    private void handleAscend(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long uid = ServletUtil.getLongParameter(req, Params.UID, 0L);
        String heroHash = ServletUtil.getStringParameter(req, Params.ID, "");
        String[] hash = ServletUtil.getStringParameter(req, Params.HASH, "").split(",");

        Map<String,String> mapResponse = new HashMap<>();
        mapResponse.put(Params.MESS, "Invalid request!!!");

        if (uid == 0) {
            mapResponse.put(Params.ERROR_CODE, String.valueOf(ServerConstant.ErrorCode.ERR_USER_NOT_FOUND));
            responseJson(Utils.toJson(mapResponse), resp);
            return;
        }

        ISFSObject params = new SFSObject();
        params.putLong(Params.UID, uid);
        params.putText(Params.HASH_HERO, heroHash);
        ISFSArray arrHash = new SFSArray();
        for (String h : hash) {
            arrHash.addText(h);
        }

        params.putSFSArray(Params.ModuleHero.FISSION, arrHash);
        ISFSObject rec = this.sendCmd(server, CMD.HttpCMD.ASCEND_HERO, params);
        responseJson(rec.toJson(), resp);
    }

    @WithSpan
    private void handleCancelAscend(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        String heroHash = ServletUtil.getStringParameter(req, Params.HASH, "");
        ISFSObject params = new SFSObject();
        params.putLong(Params.UID, uid);
        params.putText(Params.HASH, heroHash);
        ISFSObject rec = this.sendCmd(server, CMD.HttpCMD.CANCEL_ASCEND_HERO, params);
        responseJson(rec.toJson(), resp);
    }

    @WithSpan
    private void handleConfirmAscend(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        String heroHash = ServletUtil.getStringParameter(req, Params.HASH, "");
        String txhash = ServletUtil.getStringParameter(req, Params.TRANSACTION_ID, "");
        String[] tokenIds = ServletUtil.getStringParameter(req, Params.TOKEN, "").split(",");

        ISFSObject params = new SFSObject();
        params.putLong(Params.UID, uid);
        params.putText(Params.HASH, heroHash);
        params.putText(Params.TRANSACTION_ID, txhash);
        ISFSArray token = new SFSArray();
        for (String tk : tokenIds) {
            token.addText(tk);
        }

        params.putSFSArray(Params.TOKEN, token);
        ISFSObject rec = this.sendCmd(server, CMD.HttpCMD.CONFIRM_ASCEND_HERO, params);
        responseJson(rec.toJson(), resp);
    }

    @WithSpan
    private void getListHeroBreeding(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        ISFSObject params = new SFSObject();
        params.putLong(Params.UID, uid);
        ISFSObject rec = this.sendCmd(server, CMD.HttpCMD.GET_HERO_BREEDING, params);
        responseJson(rec.toJson(), resp);
    }

    @WithSpan
    private void getListHeroAscend(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        ISFSObject params = new SFSObject();
        params.putLong(Params.UID, uid);
        ISFSObject rec = this.sendCmd(server, CMD.HttpCMD.LIST_HERO_ASCEND, params);
        responseJson(rec.toJson(), resp);
    }

    @WithSpan
    private void getHeroAscendStats(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        String hash = ServletUtil.getStringParameter(req, Params.HASH);
        ISFSObject params = new SFSObject();
        params.putLong(Params.UID, uid);
        params.putText(Params.HASH, hash);
        ISFSObject rec = this.sendCmd(server, CMD.HttpCMD.GET_HERO_ASCEND_STATS, params);
        responseJson(rec.toJson(), resp);
    }

    @WithSpan
    private void handlerDeleteHero(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        String heroHash = ServletUtil.getStringParameter(req, Params.HASH, "");
        ISFSObject params = new SFSObject();
        params.putLong(Params.UID, uid);
        params.putText(Params.HASH, heroHash);
        ISFSObject res = this.sendCmd(server, "delete_hero", params);
        responseJson(res.toJson(), resp);
    }

    @WithSpan
    private void handlerDeleteHeroBlock(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        String heroHash = ServletUtil.getStringParameter(req, Params.HASH, "");
        ISFSObject params = new SFSObject();
        params.putLong(Params.UID, uid);
        params.putText(Params.HASH, heroHash);
        ISFSObject res = this.sendCmd(server, "delete_hero", params);
        responseJson(res.toJson(), resp);
    }

    @WithSpan
    private void handleMoveHero(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        long uid2 = ServletUtil.getLongParameter(req, Params.TO);
        String heroHash = ServletUtil.getStringParameter(req, Params.HASH, "");
        ISFSObject params = new SFSObject();
        params.putLong(Params.UID, uid);
        params.putText(Params.HASH, heroHash);
        params.putLong(Params.TO, uid2);
        ISFSObject res = this.sendCmd(server, "move_hero", params);
        responseJson(res.toJson(), resp);
    }

    private void handleMoveHeroUpstar(HttpServletRequest req, HttpServletResponse resp) {
        String server = ServletUtil.getStringParameter(req, Params.SERVER_ID);
        long uid = ServletUtil.getLongParameter(req, Params.UID);
        String heroHash = ServletUtil.getStringParameter(req, Params.HASH, "");
        ISFSObject params = new SFSObject();
        params.putLong(Params.UID, uid);
        params.putText(Params.HASH, heroHash);
        ISFSObject res = this.sendCmd(server, "move_hero_upstar", params);
        responseJson(res.toJson(), resp);
    }
}
