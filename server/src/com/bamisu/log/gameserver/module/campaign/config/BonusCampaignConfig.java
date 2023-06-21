package com.bamisu.log.gameserver.module.campaign.config;

import com.bamisu.log.gameserver.module.campaign.config.entities.CampaignBonusPackage;
import com.bamisu.gamelib.utils.Utils;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BonusCampaignConfig {
    private static BonusCampaignConfig ourInstance = new BonusCampaignConfig();

    public static BonusCampaignConfig getInstance() {

        BonusCampaignConfig mainCampaignConfig = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(System.getProperty("user.dir") + "/conf/campaign/campaignBonus.json");
            mainCampaignConfig = Utils.fromJson(IOUtils.toString(inputStream), BonusCampaignConfig.class);
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
        ourInstance = mainCampaignConfig;

        return ourInstance;
    }

    public BonusCampaignConfig() { }

    public Map<String, List<CampaignBonusPackage>> bonus = new HashMap<>();

    public List<CampaignBonusPackage> getBonusStation(int area, int station){
        return bonus.getOrDefault(area + "-" + station, new ArrayList<>());
    }
}
