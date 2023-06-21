package com.bamisu.log.gameserver.module.campaign.config;

import com.bamisu.log.gameserver.module.campaign.config.entities.Area;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 3:06 PM, 2/5/2020
 */
public class MainCampaignConfig {
    private static MainCampaignConfig instance;

    public static MainCampaignConfig getInstance() {
        if (instance == null) {
            MainCampaignConfig mainCampaignConfig = null;
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(System.getProperty("user.dir") + "/conf/campaign/main.json");
                mainCampaignConfig = Utils.fromJson(IOUtils.toString(inputStream), MainCampaignConfig.class);
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
            instance = mainCampaignConfig;
        }
        return instance;
    }

    public List<Area> area = new ArrayList<>();

    public MainCampaignConfig() {
    }

    public Area getArea(int areaID) {
        return area.get(areaID);
    }
}
