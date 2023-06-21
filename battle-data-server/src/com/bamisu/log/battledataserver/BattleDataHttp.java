package com.bamisu.log.battledataserver;

import com.bamisu.gamelib.entities.Params;
import com.bamisu.gamelib.httpserver.ServletBase;
import com.bamisu.gamelib.utils.ServletUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Create by Popeye on 10:39 AM, 12/2/2020
 */
public class BattleDataHttp extends ServletBase {
    @Override
    public void init() {
    }

    @Override
    protected void process(HttpServletRequest req, HttpServletResponse resp) {
        super.process(req, resp);
        fixHeaders(resp);
        String method = ServletUtil.getStringParameter(req, "cmd", "");
        switch (method) {
            case "getData":
                getData(req, resp);
                break;
            case "pushData":
                pushData(req, resp);
                break;
        }
    }

    private void pushData(HttpServletRequest req, HttpServletResponse resp) {
        try {
            new BattleDataModel(req.getParameter(Params.ID), req.getParameter(Params.DATA)).saveToDB(BattleDataDataController.getInstance());
            responseJson("{\"ec\": 0}", resp);
        }catch (Exception e){
            e.printStackTrace();
            responseJson("{\"ec\": 1}", resp);
        }
    }

    private void getData(HttpServletRequest req, HttpServletResponse resp) {
        try {
            responseJson(BattleDataModel.copyFromDBtoObject(req.getParameter(Params.ID), BattleDataDataController.getInstance()).toJson(), resp);
        } catch (Exception e) {
            e.printStackTrace();
            responseJson("{}", resp);
        }
    }
}
