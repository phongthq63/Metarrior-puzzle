package com.bamisu.log.sdk.telegrambot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

/**
 * Create by Popeye on 4:36 PM, 12/14/2020
 */
public class FWTelegramBotManager {
    private static FWTelegramBotManager ourInstance = new FWTelegramBotManager();
    public static FWTelegramBotManager getInstance() {
        return ourInstance;
    }
    TelegramBotsApi telegramBotsApi;


    private FWTelegramBotManager() {
        ApiContextInitializer.init();
        telegramBotsApi = new TelegramBotsApi();
    }

    public void registerBot(TelegramLongPollingBot bot) {
//        try {
//            telegramBotsApi.registerBot(bot);
//        } catch (TelegramApiRequestException e) {
//            e.printStackTrace();
//        }
    }
}
