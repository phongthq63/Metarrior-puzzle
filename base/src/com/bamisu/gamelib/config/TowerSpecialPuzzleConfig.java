package com.bamisu.gamelib.config;

import com.bamisu.gamelib.utils.Utils;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by Popeye on 8:53 AM, 1/23/2021
 */
public class TowerSpecialPuzzleConfig {
    private static TowerSpecialPuzzleConfig instance;

    public static TowerSpecialPuzzleConfig getInstance() {
        if (instance == null) {
            TowerSpecialPuzzleConfig _instance = null;
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(System.getProperty("user.dir") + "/conf/tower/special-puzzle.json");
                _instance = Utils.fromJson(IOUtils.toString(inputStream), TowerSpecialPuzzleConfig.class);
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
            instance = _instance;
        }
        return instance;
    }

    public Map<String, String> lockMap = new HashMap<>();
}
