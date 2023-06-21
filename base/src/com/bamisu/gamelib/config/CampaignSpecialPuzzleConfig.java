package com.bamisu.gamelib.config;

import com.bamisu.gamelib.utils.Utils;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by Popeye on 11:17 AM, 1/14/2021
 */
public class CampaignSpecialPuzzleConfig {
    private static CampaignSpecialPuzzleConfig instance;

    public static CampaignSpecialPuzzleConfig getInstance() {
        if (instance == null) {
            CampaignSpecialPuzzleConfig _instance = null;
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(System.getProperty("user.dir") + "/conf/campaign/special-puzzle.json");
                _instance = Utils.fromJson(IOUtils.toString(inputStream), CampaignSpecialPuzzleConfig.class);
            } catch (Exception e) {
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
            instance = _instance;
        }
        return instance;
    }

    public Map<String, String> lockMap = new HashMap<>();
}
