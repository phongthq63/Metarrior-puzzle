package com.bamisu.log.sdk.module;

import com.bamisu.log.sdk.telegrambot.FWTelegramBotManager;
import com.bamisu.log.sdk.telegrambot.TelegramAdminBot;
import com.bamisu.log.sdk.module.data.SDKDatacontroler;
import com.bamisu.log.sdk.module.sdkthriftserver.SDKThriftServer;
import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.httpserver.ServletBase;
import com.bamisu.log.sdk.module.sql.SDKsqlManager;

/**
 * Create by Popeye on 10:48 AM, 4/25/2020
 */
public class StartupServlet extends ServletBase {
    static {
        if(ConfigHandle.instance().getInt("isSDKServer") == 1){
            System.out.println("Begin start SDK thrift server ");
            SDKThriftServer.getInstance().start();
            SDKDatacontroler.getInstance();
            SDKsqlManager.getInstance();
//            FWTelegramBotManager.getInstance().registerBot(TelegramAdminBot.getInstance());
            System.out.println("Start SDK thrift server success");
        }
    }
}
