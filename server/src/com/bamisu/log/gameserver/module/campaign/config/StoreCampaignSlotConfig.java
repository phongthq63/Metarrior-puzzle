package com.bamisu.log.gameserver.module.campaign.config;

import com.bamisu.log.gameserver.module.campaign.config.entities.RewardStoreCampaignSlotVO;
import com.bamisu.log.gameserver.module.campaign.config.entities.StoreCampaignSlotVO;
import com.bamisu.gamelib.utils.Utils;
import org.apache.commons.io.IOUtils;
//import sun.nio.ch.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class StoreCampaignSlotConfig {
    private static StoreCampaignSlotConfig ourInstance;

    public static StoreCampaignSlotConfig getInstance() {
        if(ourInstance != null){
            return ourInstance;
        }


        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(System.getProperty("user.dir") + "/conf/campaign/StoreCampaignSlotConfig.json");
            ourInstance = Utils.fromJson(IOUtils.toString(inputStream), StoreCampaignSlotConfig.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ourInstance;
    }

    private StoreCampaignSlotConfig() {
    }

    public List<StoreCampaignSlotVO> list;
    public List<RewardStoreCampaignSlotVO> store;
}
