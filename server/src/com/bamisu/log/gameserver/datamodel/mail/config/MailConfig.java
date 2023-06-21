package com.bamisu.log.gameserver.datamodel.mail.config;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 3:53 PM, 10/22/2020
 */
public class MailConfig {
    public static MailConfig instance;

    public List<InitMailVO> initMails = new ArrayList<>();

    public static MailConfig getInstance(){
        if(instance == null){
            instance = Utils.fromJson(Utils.loadConfig(ServerConstant.Mail.FILE_PATH_CONFIG_MAIL), MailConfig.class);
        }

        return instance;
    }
}
